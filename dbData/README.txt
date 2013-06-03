What is in this directory:
--------------------------

cpath.sql       Contains the main table structure for cPath.
seed.sql        Seeds cPath with default data, needed for:
                default UI, external databases.
unit_test.sql   Sample data, used by various unit tests.


Implementation Notes:
---------------------
MySQL behaves slightly differently on MacOS X v. Linux.

On MacOS X, all table names are converted to lower case (no matter what).
Table identifiers are case insensitive.

On Linux, table name case is preserved.
Table identifies are case *sensitive*.

To fix this issue, all cpath tables are specified in lower case.
And, all cPath code must use lower case when identifying tables.
