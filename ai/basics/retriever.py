from .tfidf import TFIDFVectorizer
from .similarity import cosine_similarity

class SimpleRetriever:
    """
    A simple information retrieval search engine (sparse retriever) built from scratch.
    
    Concept:
    In Retrieval-Augmented Generation (RAG) pipelines, the system must search a
    knowledge base to find the most relevant context for a user's question.
    
    SimpleRetriever implements this using the classical Vector Space Model:
    1. Ingestion: It takes a list of documents (corpus), fits a TFIDFVectorizer to
       them, and converts all documents into TF-IDF vector representations. This
       matrix serves as our vector database index.
       
    2. Querying: When a user submits a query, it transforms the query text into the
       same TF-IDF vector space using the fitted vectorizer.
       
    3. Similarity Match: It calculates the Cosine Similarity between the query vector
       and every document vector in the index.
       
    4. Ranking: It sorts documents in descending order of similarity score and returns
       the top-K most relevant document chunks.
    """
    def __init__(self, tokenizer=None):
        self.vectorizer = TFIDFVectorizer(tokenizer=tokenizer)
        self.documents = []
        self.doc_vectors = []

    def fit(self, corpus):
        """
        Indexes a collection of documents by learning the vocabulary and creating
        the TF-IDF index.
        """
        self.documents = corpus
        # Fit the vectorizer and compute vector representations for all documents
        self.doc_vectors = self.vectorizer.fit_transform(corpus)
        return self

    def search(self, query, top_k=3):
        """
        Searches the corpus for the most semantically relevant documents for the query.
        
        Returns:
            List of dicts: [
                {
                    "document": str,   # The matched document text
                    "index": int,      # Original index of the document in the corpus
                    "score": float     # Cosine similarity score [0.0, 1.0]
                },
                ...
            ]
        """
        if not self.documents:
            raise ValueError("Retriever has no indexed documents. Call fit() first.")

        # 1. Transform the query text into a TF-IDF vector
        # Note: transform() returns a list of vectors, so we take the first one
        query_vector = self.vectorizer.transform([query])[0]

        # 2. Compute similarity against all document vectors in the index
        results = []
        for idx, doc_vector in enumerate(self.doc_vectors):
            score = cosine_similarity(query_vector, doc_vector)
            results.append({
                "document": self.documents[idx],
                "index": idx,
                "score": score
            })

        # 3. Sort documents by similarity score in descending order
        results.sort(key=lambda x: x["score"], reverse=True)

        # 4. Return the top K results
        return results[:top_k]
