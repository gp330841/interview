import re
from collections import defaultdict

class SimpleTokenizer:
    """
    A basic regular-expression-based word tokenizer.
    Splits text on whitespace and isolates punctuation marks.
    
    Concept:
    Before complex deep learning models can process text, it must be segmented
    into base units called "tokens". The simplest way to do this is to break
    text by word boundaries and punctuation.
    """
    def __init__(self, lowercase=True):
        self.lowercase = lowercase
        # Match words (letters/numbers/underscores) or any non-whitespace symbol (punctuation)
        self.pattern = re.compile(r"\w+|[^\w\s]")

    def tokenize(self, text):
        """
        Splits input text into a list of word/punctuation tokens.
        """
        if self.lowercase:
            text = text.lower()
        return self.pattern.findall(text)


class BPETokenizer:
    """
    Byte-Pair Encoding (BPE) sub-word tokenizer implemented from scratch.
    
    Concept:
    Word-level tokenizers suffer from a major issue: Out-Of-Vocabulary (OOV) words.
    If a model encounters a word it wasn't trained on (e.g., "unfriendly"), it fails.
    BPE solves this by tokenizing sub-word units. It builds a vocabulary by starts
    with individual characters and iteratively merging the most frequent adjacent 
    character/token pairs.
    
    This ensures that:
    1. Common words stay intact (e.g., "the", "and").
    2. Rare/new words are split into known sub-words (e.g., "un-friendly" or "u-n-f-r-i-e-n-d-l-y").
    3. There are zero OOV tokens because every character can be represented.
    """
    def __init__(self, vocab_size=100):
        self.vocab_size = vocab_size
        self.merges = {}         # Maps (token_a, token_b) -> merged_token_string
        self.vocab = {}          # Maps token_string -> token_id
        self.inverse_vocab = {}  # Maps token_id -> token_string
        self.special_token = "</w>"  # Denotes the end of a word (boundary marker)

    def _get_stats(self, word_counts):
        """
        Scans all current word tokenizations and counts the frequency of all
        adjacent token pairs.
        
        Example:
            word_counts = {('h', 'u', 'g', '</w>'): 5}
            pairs = {('h', 'u'): 5, ('u', 'g'): 5, ('g', '</w>'): 5}
        """
        pairs = defaultdict(int)
        for word_tuple, count in word_counts.items():
            for i in range(len(word_tuple) - 1):
                pair = (word_tuple[i], word_tuple[i+1])
                pairs[pair] += count
        return pairs

    def _merge_vocab(self, pair, word_counts):
        """
        Updates the corpus by merging every occurrence of the target pair.
        
        Example:
            pair = ('h', 'u')
            word_counts = {('h', 'u', 'g', '</w>'): 5}
            Output: {('hu', 'g', '</w>'): 5}
        """
        new_word_counts = {}
        target_first, target_second = pair
        merged_token = target_first + target_second
        
        for word_tuple, count in word_counts.items():
            new_word = []
            i = 0
            while i < len(word_tuple):
                # Check if we find the pair adjacent in this word
                if i < len(word_tuple) - 1 and word_tuple[i] == target_first and word_tuple[i+1] == target_second:
                    new_word.append(merged_token)
                    i += 2  # Skip both because they were merged
                else:
                    new_word.append(word_tuple[i])
                    i += 1
            new_word_counts[tuple(new_word)] = count
            
        return new_word_counts

    def fit(self, text):
        """
        Learns BPE merge rules and vocabulary from a text corpus.
        
        Step-by-step:
        1. Pre-tokenize text into words.
        2. Split words into character lists, adding </w> at the end.
        3. Compute initial vocabulary (all unique characters + </w>).
        4. Count adjacent token pairs.
        5. Merge the most frequent pair, add it to vocabulary and merges dictionary.
        6. Repeat until target vocab_size is achieved.
        """
        # Pre-tokenize to words
        simple_tok = SimpleTokenizer(lowercase=True)
        words = simple_tok.tokenize(text)
        
        # 1. Initialize words as tuples of characters with end-of-word suffix
        word_counts = {}
        for w in words:
            char_tuple = tuple(list(w)) + (self.special_token,)
            word_counts[char_tuple] = word_counts.get(char_tuple, 0) + 1

        # 2. Extract base character vocabulary
        unique_chars = set()
        for char_tuple in word_counts:
            unique_chars.update(char_tuple)

        # 3. Create initial vocabulary mapping
        base_vocab = sorted(list(unique_chars))
        self.vocab = {char: idx for idx, char in enumerate(base_vocab)}
        
        # Calculate how many merges we need to perform
        num_merges_needed = self.vocab_size - len(self.vocab)
        
        if num_merges_needed <= 0:
            # Capped by requested size
            self.vocab = {char: idx for idx, char in enumerate(base_vocab[:self.vocab_size])}
            self.inverse_vocab = {idx: char for char, idx in self.vocab.items()}
            return

        # 4. Iterative merge loop
        for _ in range(num_merges_needed):
            pairs = self._get_stats(word_counts)
            if not pairs:
                break  # No more pairs left to merge (all words are fully merged)
                
            # Choose the most frequent pair
            best_pair = max(pairs, key=pairs.get)
            merged_token = best_pair[0] + best_pair[1]
            
            # Record merge rule & add to vocabulary
            self.merges[best_pair] = merged_token
            self.vocab[merged_token] = len(self.vocab)
            
            # Apply merge to words corpus representation
            word_counts = self._merge_vocab(best_pair, word_counts)

        # Build reverse mapping
        self.inverse_vocab = {idx: char for char, idx in self.vocab.items()}

    def encode(self, text):
        """
        Tokenizes text into sub-word token IDs using the learned merges.
        """
        if not self.vocab:
            raise ValueError("Tokenizer has not been fitted yet! Call fit() first.")

        simple_tok = SimpleTokenizer(lowercase=True)
        words = simple_tok.tokenize(text)
        
        encoded_ids = []
        for word in words:
            # Start by breaking the word into its characters + end marker
            word_tokens = list(word) + [self.special_token]
            
            # Apply merge rules in the chronological order they were learned
            for pair, merged in self.merges.items():
                new_tokens = []
                i = 0
                while i < len(word_tokens):
                    if i < len(word_tokens) - 1 and word_tokens[i] == pair[0] and word_tokens[i+1] == pair[1]:
                        new_tokens.append(merged)
                        i += 2
                    else:
                        new_tokens.append(word_tokens[i])
                        i += 1
                word_tokens = new_tokens
                
            # Convert final tokens into vocabulary IDs
            for token in word_tokens:
                if token in self.vocab:
                    encoded_ids.append(self.vocab[token])
                # Skip unknown tokens (should be rare since base vocabulary covers all initial characters)
                
        return encoded_ids

    def decode(self, ids):
        """
        Reconstructs original text string from a list of token IDs.
        """
        tokens = [self.inverse_vocab.get(idx, "") for idx in ids]
        
        decoded_text = ""
        for token in tokens:
            if token.endswith(self.special_token):
                # Replace boundary marker with space
                decoded_text += token[:-len(self.special_token)] + " "
            else:
                decoded_text += token
                
        return decoded_text.strip()
