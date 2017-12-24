# Loader

Loads run(s) into database.

## Usage

```
$ ./gradlew installDist
$ cd build/install/loader
$ bin/loader PARAMS
```

```
The first argument must be load, load-multiple, add-images, or store-files

        load                    Loads a single run into database
        load-multiple                   Loads multiple runs into database

        add-images                      Add images for process models
        store-files                     Copy files to database folder structure
```


