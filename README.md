# Search-Engine

# INTRODUCTION

This is our Search Engine Project. This project divided into two parts:

* Part A - read files in corpus, parse and index the word in dictionary:
Reading files from a given corpus, segmenting them into documents Parsing the corpus in batches of 4000 documents, one by one. The parsing could be executed with or without stemming Indexing the terms of each batch: creating posting files and writing information about the terms into them. In addition, we create a file containing information about all the parsed documents and a united dictionary for the entire corpus.

* Part B - search and rank
Loading the posting files and dictionaries generated in part A The dictionaries are restored and loaded into memory. After entering a query: The query is parsed. Its term objects are reconstructed from the dictionary If semantics is enabled the query terms are sent to the semantics model. The query terms and semantics terms (if enabled) are sent to the ranker who finds the 50 most relevant documents.

# INFO
Project name: SearchEngine java version: java 1.8

# OPERATIONS
* Part A-
Run the project jar file

Select a corpus path in the first text area by pressing browse.

locate the stop word file in the corpus path with the name 'stopWords'.

Select a posting files path.

Click 'start' to run processing the corpus.

* Part B-
Press browse button to select an index path

If the 'stemming' option was selected on the corpus in part A, than select the stemming option again.

Press 'load dictionary' button to load the dictionary to the memory

Type query in the Query text area, you can enable semantic option and press search or press browse button to select an text file path to enter some queries together, you can enable semantic optionan and press search button.

Now, wait until the searching process is finish, it will show you the results.

If you would like to choose document to see its entities. close the results window
and choose a query from choice menu, choose a document (according its id) from choice menu and press 'show the entity' button.

# POST PROCESSING
Delete all button : clicking this button will delete all content in the selected posting files path

Load dictionary button: will load the term dictionary to memory

Show the dictionary button: shows all the unique terms in the corpus with this total tf
