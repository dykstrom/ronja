# Opening book

`OpeningBookParser.parse` reads the file named by `AppConfig.getBookFilename`,
which `ronja.properties` sets to `book.csv`. Each line has three fields separated
by semicolons: the moves leading to a position, the book move and its weight after
a slash, and a comment. The parser skips empty lines and lines starting with `#`,
and builds a map from `Position` to a list of `BookMove`. `OpeningBook` converts
the weights of a position to percentages and picks at random among its moves.

If the file cannot be read or parsed, `Ronja.createGame` logs the failure and
falls back to `OpeningBook.DEFAULT`, which holds only a few opening moves.
`OpeningBookConverter`, in the test sources, converts the book between CAN and SAN
notation.

## Where the book comes from

The book is hand-written. Opening lines are taken from openly available sources on
the internet, including the weights. Nothing generates `book.csv` from a game
database, so a new line means editing the file by hand.

## Tolerance for bad lines

A line whose moves are illegal is logged at warning level and skipped, so the rest
of the book still loads. A line with the wrong number of fields is different: the
parser throws `ParseException`, and the engine falls back to `OpeningBook.DEFAULT`
with the whole file unused. An illegal move in the book therefore fails quietly,
and only the log records it.
