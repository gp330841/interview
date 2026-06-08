import unittest
import math
from ai.basics.tokenizers import SimpleTokenizer, BPETokenizer
from ai.basics.tfidf import TFIDFVectorizer
from ai.basics.similarity import dot_product, cosine_similarity, euclidean_distance
from ai.basics.retriever import SimpleRetriever

class TestSimpleTokenizer(unittest.TestCase):
    def test_basic_tokenization(self):
        tokenizer = SimpleTokenizer(lowercase=True)
        text = "Hello, world! This is AI Engineering."
        tokens = tokenizer.tokenize(text)
        expected = ["hello", ",", "world", "!", "this", "is", "ai", "engineering", "."]
        self.assertEqual(tokens, expected)

    def test_lowercase_false(self):
        tokenizer = SimpleTokenizer(lowercase=False)
        text = "Hello World!"
        tokens = tokenizer.tokenize(text)
        self.assertEqual(tokens, ["Hello", "World", "!"])


class TestBPETokenizer(unittest.TestCase):
    def test_fit_and_encode_decode(self):
        # Small corpus with repetitive patterns
        corpus = "hug hug hug pug pug mug"
        tokenizer = BPETokenizer(vocab_size=20)
        tokenizer.fit(corpus)
        
        # Test encode
        encoded = tokenizer.encode("hug pug")
        self.assertIsInstance(encoded, list)
        self.assertTrue(all(isinstance(idx, int) for idx in encoded))
        
        # Test decode
        decoded = tokenizer.decode(encoded)
        self.assertEqual(decoded, "hug pug")

    def test_vocab_size_constraint(self):
        corpus = "abc"
        # 3 base chars ('a', 'b', 'c') + 1 boundary marker ('</w>') = 4 vocab items.
        # Request vocab_size of 5 (allowing 1 merge)
        tokenizer = BPETokenizer(vocab_size=5)
        tokenizer.fit(corpus)
        self.assertLessEqual(len(tokenizer.vocab), 5)


class TestTFIDFVectorizer(unittest.TestCase):
    def test_tfidf_computation(self):
        # A simple corpus of 3 documents
        corpus = [
            "the cat",
            "the dog",
            "the cat and the dog"
        ]
        vectorizer = TFIDFVectorizer()
        matrix = vectorizer.fit_transform(corpus)
        
        # Total documents N = 3
        # Vocabulary should be: ['and', 'cat', 'dog', 'the']
        expected_vocab = ["and", "cat", "dog", "the"]
        self.assertEqual(vectorizer.get_feature_names(), expected_vocab)
        
        # Check Document Frequencies (DF):
        # 'the' appears in 3 docs. Smoothed IDF: ln((1+3)/(1+3)) + 1 = ln(1) + 1 = 1.0
        # 'cat' appears in 2 docs. Smoothed IDF: ln((1+3)/(1+2)) + 1 = ln(1.3333333333333333) + 1 = 1.287682
        # 'and' appears in 1 doc. Smoothed IDF: ln((1+3)/(1+1)) + 1 = ln(2) + 1 = 1.693147
        
        self.assertAlmostEqual(vectorizer.idf_["the"], 1.0)
        self.assertAlmostEqual(vectorizer.idf_["cat"], math.log(4.0/3.0) + 1)
        self.assertAlmostEqual(vectorizer.idf_["and"], math.log(4.0/2.0) + 1)
        
        # Check L2 Normalization:
        # Every document vector should have an L2 norm of 1.0 (or 0.0 if empty)
        for vector in matrix:
            l2_norm = math.sqrt(sum(val ** 2 for val in vector))
            self.assertAlmostEqual(l2_norm, 1.0)


class TestSimilarityMetrics(unittest.TestCase):
    def test_dot_product(self):
        v1 = [1, 2, 3]
        v2 = [4, 5, 6]
        # 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
        self.assertEqual(dot_product(v1, v2), 32)
        
    def test_cosine_similarity(self):
        v1 = [1, 0, 0]
        v2 = [1, 0, 0]
        self.assertAlmostEqual(cosine_similarity(v1, v2), 1.0)
        
        v3 = [0, 1, 0]
        self.assertAlmostEqual(cosine_similarity(v1, v3), 0.0)
        
        v4 = [-1, 0, 0]
        self.assertAlmostEqual(cosine_similarity(v1, v4), -1.0)

    def test_euclidean_distance(self):
        v1 = [0, 0]
        v2 = [3, 4]
        # sqrt((3-0)^2 + (4-0)^2) = sqrt(9 + 16) = 5
        self.assertAlmostEqual(euclidean_distance(v1, v2), 5.0)

    def test_validation_errors(self):
        # Empty vectors
        with self.assertRaises(ValueError):
            dot_product([], [])
            
        # Dimension mismatch
        with self.assertRaises(ValueError):
            cosine_similarity([1, 2], [1, 2, 3])


class TestSimpleRetriever(unittest.TestCase):
    def test_retriever_search(self):
        corpus = [
            "We build search engines with Python.",
            "Retrieval Augmented Generation utilizes vector search.",
            "AI engineering is about combining models and databases."
        ]
        retriever = SimpleRetriever()
        retriever.fit(corpus)
        
        # Search query matching document 1 best
        results = retriever.search("augmented generation", top_k=2)
        
        # The top result should be index 1 ("Retrieval Augmented Generation...")
        self.assertEqual(results[0]["index"], 1)
        self.assertTrue(results[0]["score"] > 0)
        self.assertEqual(len(results), 2)
        
        # Verify result scores are in descending order
        self.assertTrue(results[0]["score"] >= results[1]["score"])


if __name__ == "__main__":
    unittest.main()
