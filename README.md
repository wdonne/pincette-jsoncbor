# Convert JSON to CBOR and Back

This is a command-line tool that reads from stdin and writes to stdout. If it detects CBOR on the input stream it will produce JSON and otherwise the input stream is supposed to contain JSON. In that case it generates CBOR.

Build it with ```mvn package``` and the run it as follows:

```
> java -jar target/pincette-jsoncbor-<version>-jar-with-dependencies.jar < in > out
```