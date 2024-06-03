## Design and approach to the solution

### Parsing part

I designed a robust JSON parser using a stack-based approach and the builder pattern. The lexer contains a tokenizer, which is widely used in training AI models to process and narrow down the input dimension., while the parser uses a stack to handle nested objects and arrays. The builder pattern ensures each JSON element is constructed correctly, even with missing attributes, by setting defaults for absent fields. 
I think this combination ensures flexibility and resilience in parsing incomplete JSON data.

### Generating JSON part
The mapToJson function iterates through the map entries, appending keys and values to the JSON string, handling nested maps and lists by calling appropriate helper functions. The listToJson function processes lists similarly, ensuring correct indentation and formatting.