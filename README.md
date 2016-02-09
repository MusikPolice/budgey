# budgey
A budgeting utility for people with dollars and sense.

## Downloading and compiling
To run budgey, you'll need a few prerequisites:

* Java JRE v1.8 or greater
* git v1.9.1 or greater
* maven v3.0.5 or greater

After cloning the repository, build the project with maven:

```
mvn clean package
```

This will produce an executable file called budgey.jar file in your target directory.

## Importing transactions
Currently, budgey is capable of parsing transactions from these financial institutions:

* Royal Bank of Canada
* Scotiabank

If your institution isn't supported, consider putting together a pull request that adds support. If you aren't able to contribute code, create an issue and we'll try to get to it as soon as possible.

To import transactions, first export them from your bank in *.csv format. The process for doing this varies by institution, so you're on your own here.

Once you have a *.csv file, you can import it into budgey using the `import` utility:

```
java -jar budgey.jar import -f /path/to/file.csv
```

The default importer attempts to parse Royal Bank *.csv files. Other institutions can be selected with the `-p` option.

## So what does it do?
Right now, not much of anything, but we've got a lot of ideas on how to make this thing useful. If have ideas of your own, we encourage you to put together a pull request that adds the functionality that you want to see. If you aren't able to contribute code, create an issue and we'll try to get to it as soon as possible.