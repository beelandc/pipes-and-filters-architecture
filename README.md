# pipes-and-filters-architecture-example
Java implementation of a pipes and filters architecture

Developed for SE480 - Software Architecture

The application is compiled into a jar that has CLI options:
```
Usage: 
 -e,--fileEncoding          The encoding of the file. Defaults to UTF-8
 -f,--fileToProcess <arg>   The complete path to the file to process
 -h,--help                  show help.
```

As an example, to process a UTF-8 text file on your filesystem:

`java -jar CBeeland_SE480_Assignment4-0.0.1-SNAPSHOT.jar -f /complete/path/to/textfile.txt `

The application will output the top 10 words by frequency to StdOut like so:

```
Top Words by Frequency:
-----------------------
lord - 8006
god - 4716
thy - 4600
ye - 3983
will - 3843
thee - 3827
son - 3486
king - 2884
man - 2735
dai - 2615
```
