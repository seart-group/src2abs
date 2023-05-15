# src2abs
`src2abs` is a library that *abstracts* Java source code.
This version in particular was created for simpler integration into [DL4SE](https://github.com/seart-group/DL4SE).

It transforms source code, such as:

``` java
public static void main(String[] args) {
    console.println("Hello, World!");
}
```

Into an equivalent abstract textual representation:

```java
public static void METHOD_1 ( TYPE_1 [ ] VAR_1 ) { VAR_2 . METHOD_2 ( STRING_1 ) ; }
```

Each abstract representations contains:
- Java Keywords;
- Code Separators;
- IDs in place of identifiers and literals; 
- Idioms (optionally).

## How it works
`src2abs` uses a Java Lexer to read and tokenize the source code.
A Java Parser analyzes the code and discerns the type of each identifier and literal in the source code.
Next, `src2abs` replaces all identifiers and literals in the stream of tokens with a unique ID which represents the type and role of the identifier/literal in the code.

Each ID `<TYPE>_#` is formed by a prefix (_i.e.,_ `<TYPE>_...`) which represents the type and role of the identifier/literal,
and a numerical ID (_i.e.,_ `..._#`) which is assigned sequentially when reading the code.
Note that these IDs are reused when the same identifier/literal appears again in the stream of tokens.
Here is the list of supported IDs: 

### Identifiers

- `TYPE_#`
- `METHOD_#`
- `VAR_#`

### Literals

- `INT_#`
- `FLOAT_#`
- `CHAR_#`
- `STRING_#`

## Idioms
There are some identifiers and literals that occur frequently enough in source code that they can almost be considered keywords of the language.
For example, the variable names `i`, `index`, the method names `toString()`, `indexOf()`, literals such as `0`, `\n`, `1`, etc.,
provide meaningful semantic information that can be helpful in a variety of tasks.
We refer to these frequent identifiers and literals as *idioms*.

`src2abs` allows to specify a list of idioms (either identifier or literal values) that will be kept in the abstract representation and not replaced with IDs.
For example, if the idioms `String` (a common Java type) and `args` (a common variable name) are specified, then `src2abs` will generate the following abstract source code for the previous example:

```java
public static void METHOD_1 ( String [ ] args ) { VAR_1 . METHOD_2 ( STRING_1 ) ; }
```

## Installation

You can compile the library as a Maven dependency:

```shell
mvn clean package
```

However, if you want to use it directly, you have to compile the executable JAR (containing all the dependencies):
```shell
mvn clean compile assembly:single
```

## Usage

```shell
java -jar src2abs-executable.jar [-hV] [-g <granularity>] [-i <idioms>] [-o <output>] <input>
```

Arguments:
```
  <input>                           Path to file containing the source code used as input.
  -g, --granularity <granularity>   The granularity level that abstraction will be
                                    performed on. Can be one of: METHOD, CLASS.
                                    Default: CLASS.
  -i, --idioms <idioms>             Path to the file containing a newline-separated list of idioms.
  -o, --output <output>             Path to file which will contain the abstraction result.
                                    If not specified, the abstraction result
                                    and mappings are printed to console.
  -h, --help                        Show this help message and exit.
  -V, --version                     Print version information and exit.
```

## Credits

`src2abs` was created by [Michele Tufano](http://www.cs.wm.edu/~mtufano/) and [Cody Watson](http://www.cs.wm.edu/~cawatson/)
and used in the context of the following research projects:

1. On Learning Meaningful Code Changes via Neural Machine Translation
2. An Empirical Study on Learning Bug-Fixing Patches in the Wild via Neural Machine Translation

If you are using `src2abs` for research purposes, please cite according to the provided [citation file](CITATION.bib).
