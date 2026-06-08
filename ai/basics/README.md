# AI Engineering Basics: From-Scratch Concept Guide

This module contains from-scratch implementations of fundamental AI Engineering concepts. Before using high-level libraries (like HuggingFace `tokenizers`, `scikit-learn`, or vector databases), it is critical to master the underlying mathematical and algorithmic primitives.

---

## 1. Sub-Word Tokenization & Byte-Pair Encoding (BPE)

Deep learning models cannot read raw text. Text must be converted into numerical sequences by segmenting it into **tokens** and mapping those tokens to **Vocabulary IDs**.

### The Vocabulary Trade-Off
* **Word-Level Tokenization**: Splits on whitespace/punctuation.
  * *Problem*: Massive vocabulary sizes (millions of words) and fails on **Out-Of-Vocabulary (OOV)** words (e.g., if the model never saw "unfriendly", it fails).
* **Character-Level Tokenization**: Splits text into individual letters (`u`, `n`, `f`, ...).
  * *Problem*: Sequences become extremely long, diluting context, and individual letters lose semantic meaning.
* **Sub-word Tokenization (BPE)**: The industry standard (used by GPT, Llama).
  * *Solution*: Starts with characters, and merges the most frequent adjacent byte/character pairs. Common words remain whole; rare or unseen words are split into recognizable sub-word units (e.g., `"un"` + `"friendly"`).

### How the BPE Training Algorithm Works (From Scratch)
1. **Pre-tokenize** the training corpus into words.
2. Initialize the vocabulary with all unique base characters plus a special end-of-word token `</w>`.
3. Separate each word into characters: `"hug"` becomes `('h', 'u', 'g', '</w>')`.
4. Count all adjacent pairs (bigrams) in the corpus.
5. Identify the most frequent pair and merge them into a single token (e.g., `('h', 'u')` $\rightarrow$ `'hu'`).
6. Repeat steps 4–5 until reaching the target vocabulary size.

---

## 2. Term Frequency - Inverse Document Frequency (TF-IDF)

TF-IDF is the foundational algorithm for **sparse vector representations**. It translates documents into numerical vectors based on word frequencies, scaling down words that occur too frequently across the entire corpus.

### Mathematical Formulations

#### 1. Term Frequency (TF)
Measures the local importance of a word $t$ inside a single document $d$.
$$\text{tf}(t, d) = \text{count}(t \text{ in } d)$$

#### 2. Document Frequency (DF)
The number of documents in the corpus $D$ that contain the term $t$:
$$\text{df}(t) = |\{d \in D : t \in d\}|$$

#### 3. Inverse Document Frequency (IDF)
Measures the global importance (or informational value) of a word. Rare words get a high score; common words get a low score. We use logarithmic scaling and add $1$ as a smoothing factor to ensure IDF does not drop to zero:
$$\text{idf}(t) = \ln\left(\frac{1 + N}{1 + \text{df}(t)}\right) + 1$$
*(where $N$ is the total number of documents in the corpus, $|D|$).*

#### 4. TF-IDF Score
$$\text{raw\_tfidf}(t, d) = \text{tf}(t, d) \times \text{idf}(t)$$

#### 5. L2 Normalization
To ensure document length does not skew similarity, we scale each document vector $\mathbf{v}$ to unit length ($\|\mathbf{v}\|_2 = 1.0$):
$$\mathbf{v}_{\text{normalized}} = \frac{\mathbf{v}}{\|\mathbf{v}\|_2} = \frac{\mathbf{v}}{\sqrt{\sum v_i^2}}$$

---

### Worked Paper Example: Step-by-Step

Let's manually compute the TF-IDF representation for a corpus of $N = 3$ documents.

* **Doc 1**: `"cat sat"`
* **Doc 2**: `"cat slept"`
* **Doc 3**: `"dog sat"`

#### Step 1: Extract Vocabulary & Indexing
Sorted unique terms: `["cat", "dog", "sat", "slept"]`
* Index `0`: `"cat"`
* Index `1`: `"dog"`
* Index `2`: `"sat"`
* Index `3`: `"slept"`

#### Step 2: Compute Document Frequencies (DF)
* `cat` appears in: Doc 1, Doc 2. $\rightarrow \text{df} = 2$
* `dog` appears in: Doc 3. $\rightarrow \text{df} = 1$
* `sat` appears in: Doc 1, Doc 3. $\rightarrow \text{df} = 2$
* `slept` appears in: Doc 2. $\rightarrow \text{df} = 1$

#### Step 3: Compute Smoothed IDF Scores
Formula: $\text{idf}(t) = \ln\left(\frac{1 + N}{1 + \text{df}(t)}\right) + 1$ (where $N = 3$, so $1+N = 4$):

* **`cat`**: $\text{idf} = \ln(4 / 3) + 1 \approx 0.2877 + 1 = 1.2877$
* **`dog`**: $\text{idf} = \ln(4 / 2) + 1 = \ln(2) + 1 \approx 0.6931 + 1 = 1.6931$
* **`sat`**: $\text{idf} = \ln(4 / 3) + 1 \approx 0.2877 + 1 = 1.2877$
* **`slept`**: $\text{idf} = \ln(4 / 2) + 1 = \ln(2) + 1 \approx 0.6931 + 1 = 1.6931$

*Notice: The rarer words (`dog`, `slept`) have higher IDF scores than the common ones (`cat`, `sat`).*

#### Step 4: Compute Raw TF-IDF Vectors
Multiply local frequency (TF) by the word's IDF score:

* **Doc 1**: `cat` = 1, `sat` = 1
  * $\text{raw\_tfidf} = [1 \times 1.2877,\, 0.0,\, 1 \times 1.2877,\, 0.0] = [1.2877,\, 0.0,\, 1.2877,\, 0.0]$
* **Doc 2**: `cat` = 1, `slept` = 1
  * $\text{raw\_tfidf} = [1 \times 1.2877,\, 0.0,\, 0.0,\, 1 \times 1.6931] = [1.2877,\, 0.0,\, 0.0,\, 1.6931]$
* **Doc 3**: `dog` = 1, `sat` = 1
  * $\text{raw\_tfidf} = [0.0,\, 1 \times 1.6931,\, 1 \times 1.2877,\, 0.0] = [0.0,\, 1.6931,\, 1.2877,\, 0.0]$

#### Step 5: Apply L2 Normalization
Divide each vector by its Euclidean length (norm):

* **Doc 1 Norm**: $\sqrt{1.2877^2 + 1.2877^2} = \sqrt{1.6582 + 1.6582} = \sqrt{3.3164} \approx 1.8211$
  * $\mathbf{v}_{1\_norm} = [1.2877 / 1.8211,\, 0.0,\, 1.2877 / 1.8211,\, 0.0] \approx \mathbf{[0.7071,\, 0.0,\, 0.7071,\, 0.0]}$
* **Doc 2 Norm**: $\sqrt{1.2877^2 + 1.6931^2} = \sqrt{1.6582 + 2.8666} = \sqrt{4.5248} \approx 2.1272$
  * $\mathbf{v}_{2\_norm} = [1.2877 / 2.1272,\, 0.0,\, 0.0,\, 1.6931 / 2.1272] \approx \mathbf{[0.6053,\, 0.0,\, 0.0,\, 0.7959]}$
* **Doc 3 Norm**: $\sqrt{1.6931^2 + 1.2877^2} \approx 2.1272$
  * $\mathbf{v}_{3\_norm} = [0.0,\, 1.6931 / 2.1272,\, 1.2877 / 2.1272,\, 0.0] \approx \mathbf{[0.0,\, 0.7959,\, 0.6053,\, 0.0]}$

---

## 3. Vector Similarity Metrics

Once text is projected into vector space, we calculate similarities using:

| Metric | Formula | Character / Use Cases |
| :--- | :--- | :--- |
| **Dot Product** | $$\mathbf{a} \cdot \mathbf{b} = \sum a_i b_i$$ | Measures alignment. Sensitive to magnitude. Fast but skewed by document length. |
| **Cosine Similarity** | $$\cos(\theta) = \frac{\mathbf{a} \cdot \mathbf{b}}{\|\mathbf{a}\| \|\mathbf{b}\|}$$ | Measures the angle between vectors (ignores magnitude). Ideal for text similarity. |
| **Euclidean Distance (L2)** | $$d = \sqrt{\sum (a_i - b_i)^2}$$ | Measures spatial straight-line distance. Smaller = more similar. |

> [!TIP]
> If vectors are **L2-Normalized** (length of $1.0$), then:
> $$\cos(\theta) = \mathbf{a} \cdot \mathbf{b}$$
> The Cosine Similarity simplifies to the Dot Product, which is mathematically cheaper to compute in production vector indices.

---

## 4. Sparse Retrieval (Vector Search)

In **Retrieval-Augmented Generation (RAG)** systems, a user query must be matched to document chunks. 
1. The **Query** is transformed using the fitted TFIDFVectorizer.
2. Cosine similarities between the query vector and all document vectors are calculated.
3. The closest matches are returned to enrich the LLM's prompt window.

---

## 🚀 How to Run and Learn

### 1. Run the Interactive Demo
Run the demo script to view step-by-step vocabulary learning, BPE merges, and TF-IDF RAG retrieval in the terminal:
```bash
python3 ai/basics/demo.py
```

### 2. Run the Unit Tests
Verify the mathematical calculations against scikit-learn standard formulas using the unit tests:
```bash
python3 -m unittest ai/basics/test_basics.py
```
