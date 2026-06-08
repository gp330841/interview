import math
from collections import Counter
from .tokenizers import SimpleTokenizer

class TFIDFVectorizer:
    """
    Term Frequency - Inverse Document Frequency (TF-IDF) Vectorizer from scratch.
    
    Concept:
    In search engines or simple classifiers, we need to convert text documents
    into numerical feature vectors. A naive approach is to count occurrences 
    (Bag of Words), but this overvalues common words like "the" or "is".
    
    TF-IDF fixes this by scaling counts by how informative a word is:
    1. Term Frequency (TF): How often a word appears in a specific document.
       If a word appears often in a document, it must be highly relevant to it.
       Formula: tf(t, d) = count(t in d)
       
    2. Document Frequency (DF): How many documents in the entire corpus contain the word.
    
    3. Inverse Document Frequency (IDF): Measures a word's rarity across the corpus.
       If a word appears in almost all documents (like "the"), its IDF is low (near 1).
       If a word is rare (like "quantum"), its IDF is high.
       Formula (with smoothing to prevent division by zero):
       idf(t) = ln((1 + N) / (1 + df(t))) + 1
       
    4. TF-IDF Score: tf(t, d) * idf(t)
    
    5. L2 Normalization: We normalize each document vector so its total L2 norm is 1.
       This prevents longer documents (with higher raw term counts) from dominating.
       It also means the dot product of two normalized vectors is exactly their Cosine Similarity.
    """
    def __init__(self, tokenizer=None):
        # Default to SimpleTokenizer if none provided
        self.tokenizer = tokenizer if tokenizer else SimpleTokenizer(lowercase=True)
        self.vocabulary_ = {}       # Maps term -> index
        self.idf_ = {}              # Maps term -> IDF score
        self.feature_names_ = []    # List of terms indexed by their position

    def fit(self, raw_documents):
        """
        Learns the vocabulary and calculates IDF scores from a training corpus.
        """
        # 1. Tokenize all documents and count total docs (N)
        tokenized_docs = [self.tokenizer.tokenize(doc) for doc in raw_documents]
        N = len(raw_documents)
        
        # 2. Identify unique vocabulary words
        unique_terms = set()
        for doc in tokenized_docs:
            unique_terms.update(doc)
            
        # Sort vocabulary alphabetically for deterministic indexing (like scikit-learn)
        self.feature_names_ = sorted(list(unique_terms))
        self.vocabulary_ = {term: idx for idx, term in enumerate(self.feature_names_)}
        
        # 3. Calculate Document Frequency (DF) for each term
        df = Counter()
        for doc in tokenized_docs:
            # Set ensures we count a word at most once per document
            unique_words_in_doc = set(doc)
            for word in unique_words_in_doc:
                df[word] += 1
                
        # 4. Calculate smoothed IDF for each term in the vocabulary
        # Formula: idf(t) = ln((1 + N) / (1 + df(t))) + 1
        for term in self.vocabulary_:
            df_t = df[term]
            self.idf_[term] = math.log((1 + N) / (1 + df_t)) + 1
            
        return self

    def transform(self, raw_documents):
        """
        Converts raw documents into L2-normalized TF-IDF vectors.
        """
        if not self.vocabulary_:
            raise ValueError("Vectorizer must be fitted before transforming. Call fit() first.")
            
        vectors = []
        for doc in raw_documents:
            tokens = self.tokenizer.tokenize(doc)
            term_counts = Counter(tokens)
            
            # Create raw TF-IDF vector (all zeros initially)
            vector = [0.0] * len(self.vocabulary_)
            
            # Compute raw TF-IDF scores for words in this document that are in our vocabulary
            for term, count in term_counts.items():
                if term in self.vocabulary_:
                    idx = self.vocabulary_[term]
                    tf = count
                    idf = self.idf_[term]
                    vector[idx] = tf * idf
            
            # Apply L2 Normalization: v_norm = v / sqrt(sum(v_i^2))
            l2_norm = math.sqrt(sum(val ** 2 for val in vector))
            if l2_norm > 0:
                vector = [val / l2_norm for val in vector]
                
            vectors.append(vector)
            
        return vectors

    def fit_transform(self, raw_documents):
        """
        Fits to the corpus and returns the L2-normalized TF-IDF document-term matrix.
        """
        return self.fit(raw_documents).transform(raw_documents)

    def get_feature_names(self):
        """
        Returns the vocabulary terms in order of their feature index.
        """
        return self.feature_names_
