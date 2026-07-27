# Specification: Distributed Document Search Engine

**Spec ID**: SPEC-DS-001  
**Status**: APPROVED  
**Category**: Distributed Systems / Information Retrieval  

---

## 1. System Overview
A high-throughput distributed document search engine capable of indexing text documents, computing TF-IDF relevance scores, and executing multi-node scatter-gather search queries.

---

## 2. Functional Requirements
- **FR-01 (Indexing)**: Accept document ingestion (`docId`, `content`, `tags`) and update the concurrent inverted index.
- **FR-02 (Relevance Scoring)**: Score query term matches using Term Frequency - Inverse Document Frequency (TF-IDF).
- **FR-03 (Scatter-Gather Search)**: Query coordinator distributes search requests across multiple index shards and merges top-K ranked results.

---

## 3. Non-Functional Requirements
- **Latency**: P99 search query latency < 20 ms for 1,000,000 indexed documents.
- **Concurrency**: Fully thread-safe concurrent reads and non-blocking indexing writes.
- **Memory Footprint**: Memory-efficient term dictionary with lock-free concurrent collections.

---

## 4. Low-Level Component Design

### 4.1 Inverted Index Data Model
- `termDictionary`: `ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>`
  - Maps term -> (docId -> termFrequency)
- `documentStore`: `ConcurrentHashMap<String, Document>`
  - Maps docId -> Document object containing total token count.

### 4.2 Scoring Formula
$$\text{TF}(t, d) = \frac{\text{count}(t \in d)}{\text{totalTokens}(d)}$$

$$\text{IDF}(t) = \ln\left(1 + \frac{N}{1 + |\text{docs containing } t|}\right)$$

$$\text{Score}(q, d) = \sum_{t \in q} \text{TF}(t, d) \times \text{IDF}(t)$$

---

## 5. API Definition & Code Mapping
- `InvertedIndex#addDocument(Document doc)`
- `InvertedIndex#search(String query, int topK)`
- `SearchCoordinator#scatterGatherSearch(List<InvertedIndex> shards, String query, int topK)`
