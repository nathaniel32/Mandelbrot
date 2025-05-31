#!/bin/bash

echo "Aufruf: gnuplot datafile"

if [[ $1 = '' ]]
then
  VAR='result.txt'
else
  VAR=$1
fi

# FILE=$1 gnuplot result.plt -
FILE=$VAR gnuplot result.plt -

