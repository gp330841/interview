import re
import html

def clean_html(html_content):
    # Remove script and style elements
    html_content = re.sub(r'<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>', '', html_content, flags=re.IGNORECASE)
    html_content = re.sub(r'<style\b[^<]*(?:(?!<\/style>)<[^<]*)*<\/style>', '', html_content, flags=re.IGNORECASE)
    
    # Replace common structural tags with line breaks/markdown-ish markers
    html_content = re.sub(r'</?(h1|h2|h3|h4|h5|h6|p|div|li|tr)[^>]*>', '\n', html_content, flags=re.IGNORECASE)
    html_content = re.sub(r'</?(br|hr)[^>]*>', '\n', html_content, flags=re.IGNORECASE)
    
    # Strip all other HTML tags
    text = re.sub(r'<[^>]+>', '', html_content)
    
    # Decode HTML entities
    text = html.unescape(text)
    
    # Clean up whitespace
    lines = [line.strip() for line in text.splitlines()]
    non_empty_lines = [line for line in lines if line]
    
    return '\n'.join(non_empty_lines)

with open('/Users/yogeshwarpatel/.gemini/antigravity/brain/d02f1b1a-e55e-4df6-bdf1-ff45f44c801c/.system_generated/steps/676/content.md', 'r', encoding='utf-8') as f:
    content = f.read()

cleaned = clean_html(content)

with open('/Users/yogeshwarpatel/Workspace/interview/cleaned_text.txt', 'w', encoding='utf-8') as f:
    f.write(cleaned)

print("Cleaned text has been written to /Users/yogeshwarpatel/Workspace/interview/cleaned_text.txt")
print("Preview of first 1000 chars:")
print(cleaned[:1000])
