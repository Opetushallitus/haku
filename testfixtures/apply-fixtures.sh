#!/bin/bash

fixturesroot="../hakemus-api/src/test/resources/mongofixtures"

for d in $fixturesroot/*
do
   if [ -d $d ];then
      for f in $d/*.json
      do
         collection="${d##*/}"
         echo Collection $collection
         echo Importing $f to Mongo $@
         mongoimport "$@" -c $collection -d hakulomake --file $f --upsert
      done
   fi
done