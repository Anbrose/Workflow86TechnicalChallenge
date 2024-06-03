# Workflow86 Technical Challenge

This project is for preseting my implementation for solving the challenge, purposed by Workflow86.

## Dict structure:
```shell
├── README.md
├── Workflow86TechnicalChallenge.iml
├── output.json   # Output Sample / Parsed Data Destination
├── src
│   └── com
│       └── company
│           ├── Main.java             # Entry & Contains JSON generator
│           └── RobustJSONParser.java # JSON Parser
└── testcases  # Test cases 
    ├── case0.txt
    ├── case1.txt
    ├── case2.txt
    └── case3.txt

```


## How to use it:

0. Make sure Java is correctly installed:
```shell
java -version
javac -version
```
1. First clone this repo:
```shell
git clone https://github.com/Anbrose/Workflow86TechnicalChallenge.git
```
2. Compile the classes:
```shell
cd src
javac com/company/Main.java com/company/RobustJSONParser.java
```

3. Make jar file:
```shell
cd ..
jar cfe WorkFlowProkect.jar com.company.Main -C src .
```

4. Run the script and you shall see the result in output.json:
```shell
java -jar WorkFlowProkect.jar <path>/Workflow86TechnicalChallenge/testcases/case0.txt
vim output.json
```

## Contact me

