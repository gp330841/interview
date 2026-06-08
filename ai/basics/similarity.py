import math

def _validate_vectors(v1, v2):
    """
    Ensures that the input vectors are of the same non-zero dimension.
    """
    if len(v1) != len(v2):
        raise ValueError(f"Vector dimensions must match. Got lengths {len(v1)} and {len(v2)}.")
    if len(v1) == 0:
        raise ValueError("Vectors cannot be empty.")

def dot_product(v1, v2):
    """
    Computes the dot product (inner product) of two vectors.
    
    Concept:
    The dot product measures the alignment of two vectors.
    - If the vectors point in the same direction, the dot product is positive.
    - If they are perpendicular (orthogonal), the dot product is 0.
    - If they point in opposite directions, the dot product is negative.
    
    Formula:
    v1 . v2 = sum(v1_i * v2_i)
    
    Note: Dot product is sensitive to the magnitude (length) of the vectors. 
    A long vector with large values will have a huge dot product, which is why 
    we often normalize it.
    """
    _validate_vectors(v1, v2)
    return sum(x * y for x, y in zip(v1, v2))


def cosine_similarity(v1, v2):
    """
    Computes the cosine similarity of two vectors.
    
    Concept:
    Cosine similarity measures the cosine of the angle between two vectors in 
    an N-dimensional space. It evaluates *direction* rather than *magnitude*.
    - Cosine similarity of 1 means the vectors point in the exact same direction.
    - Cosine similarity of 0 means they are at a 90-degree angle (no similarity).
    - Cosine similarity of -1 means they are opposite.
    
    This is the standard similarity metric in NLP and vector search, as it is
    robust to document length (a document containing a keyword 10 times is 
    semantically similar to one containing it 2 times, just longer).
    
    Formula:
    cosine(v1, v2) = (v1 . v2) / (||v1|| * ||v2||)
    where ||v|| = sqrt(sum(v_i^2)) is the Euclidean norm.
    """
    _validate_vectors(v1, v2)
    
    numerator = dot_product(v1, v2)
    
    norm1 = math.sqrt(sum(x ** 2 for x in v1))
    norm2 = math.sqrt(sum(y ** 2 for y in v2))
    
    # Handle zero vectors (to prevent division by zero)
    if norm1 == 0 or norm2 == 0:
        return 0.0
        
    return numerator / (norm1 * norm2)


def euclidean_distance(v1, v2):
    """
    Computes the L2 (Euclidean) distance between two vectors.
    
    Concept:
    Euclidean distance measures the straight-line distance between two points 
    in a multi-dimensional space.
    - A distance of 0 means the points are identical.
    - The larger the distance, the more dissimilar the vectors.
    
    Unlike cosine similarity, it is highly sensitive to vector length/magnitude.
    
    Formula:
    d(v1, v2) = sqrt(sum((v1_i - v2_i)^2))
    """
    _validate_vectors(v1, v2)
    return math.sqrt(sum((x - y) ** 2 for x, y in zip(v1, v2)))
