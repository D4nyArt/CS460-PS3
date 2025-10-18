# DBSM Implementation #

This is a Database Management System (DBMS) implementation written in Java, likely for a CS460 course assignment (Problem Set 3).


## SQL Statement Classes: ##

Various SQL statement implementations: SelectStatement, InsertStatement, UpdateStatement, DeleteStatement, CreateStatement, DropStatement
Transaction control: BeginStatement, CommitStatement, RollbackStatement

## Core Database Components: ##

Table - Represents database tables
Column - Represents table columns with metadata (type, length, constraints)
Catalog - Manages table metadata storage and retrieval using Berkeley DB
DBMS - Main database management system class

## Query Processing: ##

Comparison, CompareTerm - Handle WHERE clause conditions
ConditionalExpression, AndExpression, OrExpression, NotExpression - Boolean logic
Limit - Implements LIMIT clause functionality (offset + max rows)

## Parsing: ##

Lexer - Tokenizes SQL input
Parser - Cup-generated parser (large file ~1400+ lines) that converts SQL text into statement objects
sym - Symbol constants for parser

## Data I/O: ##

RowInput, RowOutput - Handle reading/writing row data
ColumnOptions - Stores column constraints (NOT NULL, PRIMARY KEY)

## Storage Engine ##

The system uses Berkeley DB (Java Edition) for storage, as evidenced by the db directory with .jdb files and configuration
