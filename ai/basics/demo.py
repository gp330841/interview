from ai.basics.tokenizers import SimpleTokenizer, BPETokenizer
from ai.basics.tfidf import TFIDFVectorizer
from ai.basics.similarity import dot_product, cosine_similarity, euclidean_distance
from ai.basics.retriever import SimpleRetriever

def print_header(title):
    print("\n" + "=" * 60)
    print(f" {title} ".center(60, "="))
    print("=" * 60)

def main():
    # ----------------------------------------------------
    # Part 1: Tokenization and Byte-Pair Encoding (BPE)
    # ----------------------------------------------------
    print_header("Part 1: Sub-word Tokenization with Byte-Pair Encoding (BPE)")
    
    # Train BPE on a small, repetitive corpus
    bpe_corpus = "hug hug hug pug pug mug"
    print(f"1. Training BPE tokenizer on a small corpus: '{bpe_corpus}'")
    
    # Request a vocabulary size of 15 (base characters + merges)
    tokenizer = BPETokenizer(vocab_size=15)
    tokenizer.fit(bpe_corpus)
    
    print("\nLearned Merge Rules (chronological order of merges):")
    for pair, merged in tokenizer.merges.items():
        print(f"  Merge pair {pair} ---> '{merged}'")
        
    print("\nFinal Vocabulary Mapping (Token -> ID):")
    for token, idx in sorted(tokenizer.vocab.items(), key=lambda x: x[1]):
        print(f"  ID {idx:02d}: '{token}'")

    test_text = "hug pug"
    encoded_ids = tokenizer.encode(test_text)
    decoded_text = tokenizer.decode(encoded_ids)
    
    print(f"\n2. Encoding and decoding test string: '{test_text}'")
    print(f"  Encoded token IDs: {encoded_ids}")
    print(f"  Sub-words matched: {[tokenizer.inverse_vocab[idx] for idx in encoded_ids]}")
    print(f"  Decoded text:      '{decoded_text}'")


    # ----------------------------------------------------
    # Part 2: TF-IDF Vectorization
    # ----------------------------------------------------
    print_header("Part 2: Text Vectorization with TF-IDF")
    
    corpus = [
        "machine learning is cool",
        "vector databases are cool",
        "machine learning and vector search"
    ]
    
    print("Corpus documents:")
    for idx, doc in enumerate(corpus):
        print(f"  Doc {idx}: '{doc}'")
        
    vectorizer = TFIDFVectorizer()
    tfidf_matrix = vectorizer.fit_transform(corpus)
    vocab = vectorizer.get_feature_names()
    
    print("\nVocabulary indexes:")
    for term, idx in vectorizer.vocabulary_.items():
        print(f"  Index {idx}: '{term}'")
        
    print("\nComputed Inverse Document Frequency (IDF) Scores:")
    print("Formula: idf(t) = ln((1 + N) / (1 + df(t))) + 1")
    for term in vocab:
        idf = vectorizer.idf_[term]
        # df(t) is count of documents containing term
        df_count = sum(1 for doc in corpus if term in doc.lower().split())
        print(f"  Word: {term:<10} | DF: {df_count}/{len(corpus)} | IDF: {idf:.4f} (rarer words have higher IDF!)")

    print("\nL2-Normalized Document Vectors:")
    print(f"Features: {vocab}")
    for idx, vector in enumerate(tfidf_matrix):
        formatted_vector = [f"{val:.3f}" for val in vector]
        print(f"  Doc {idx}: {formatted_vector}")


    # ----------------------------------------------------
    # Part 3: Vector Similarity & Distance
    # ----------------------------------------------------
    print_header("Part 3: Vector Space Similarity Metrics")
    
    v0 = tfidf_matrix[0]  # "machine learning is cool"
    v1 = tfidf_matrix[1]  # "vector databases are cool"
    
    print("Comparing Doc 0 and Doc 1 vectors:")
    print(f"  Doc 0: {[f'{val:.3f}' for val in v0]}")
    print(f"  Doc 1: {[f'{val:.3f}' for val in v1]}")
    
    dot = dot_product(v0, v1)
    cosine = cosine_similarity(v0, v1)
    euclidean = euclidean_distance(v0, v1)
    
    print(f"\nSimilarity calculations:")
    print(f"  1. Dot Product:        {dot:.4f}  (Measures alignment and magnitude)")
    print(f"  2. Cosine Similarity:   {cosine:.4f}  (Measures angular similarity, ignoring magnitude)")
    print(f"  3. Euclidean Distance:  {euclidean:.4f}  (Measures straight-line distance, sensitive to magnitude)")
    print("\nConcept Note:")
    print("  Because our vectors are L2-normalized to a length of 1.0, the Dot Product")
    print("  and Cosine Similarity are mathematically identical!")


    # ----------------------------------------------------
    # Part 4: Information Retrieval / RAG Search
    # ----------------------------------------------------
    print_header("Part 4: Mini Search Engine (Retriever)")
    
    retriever = SimpleRetriever()
    retriever.fit(corpus)
    
    query = "machine learning vector"
    print(f"Searching the corpus for query: '{query}'")
    
    # Show how the query is transformed into a vector
    query_vec = retriever.vectorizer.transform([query])[0]
    non_zero_terms = [f"'{vocab[i]}'={query_vec[i]:.3f}" for i in range(len(vocab)) if query_vec[i] > 0]
    print(f"  Query TF-IDF vector coordinates: {non_zero_terms}")
    
    results = retriever.search(query, top_k=3)
    
    print("\nSearch Results (ranked by Cosine Similarity):")
    for rank, result in enumerate(results, 1):
        print(f"  {rank}. Doc {result['index']}: '{result['document']}'")
        print(f"     Score: {result['score']:.4f}")
        print()

if __name__ == "__main__":
    main()
