#!/bin/sh

ab="`which ab`"
gnuplot="`which gnuplot`"
eog="`which eog`"

requests=10
concurrency=10
output="out.data"

usage ()
{
   cat <<!EOF!
usage: $0 [option] [url]
options:
  -c        concurrency, default 10 
  -n        requests, default 10
  -g        gnuplot-file, default 'out.data'
  -h        Print this help summary and exit.

  See also 
    man ab
    man gnuplot
!EOF!
}

while getopts c:n:g:h opt ; do
        case "$opt" in
        c) concurrency="$OPTARG" ;;
        n) requests="$OPTARG" ;;
        g) gnuplot="$OPTARG" ;;
        u) url="$OPTARG" ;;
        h) usage; exit 0 ;;
        *) usage; exit 1 ;;
        esac
done
shift $(($OPTIND - 1))

url=$1

echo $#
echo $concurrent
echo $requests
echo $gnuplot
echo $url
if [ -z $url ]; then 
    usage; 
    exit 1; 
fi


if test ! -x "$ab"; then
    exit "Could not find ab command!"
fi

if test ! -x "$gnuplot"; then
    exit "Could not find gnuplot command!"
fi

$ab -c $concurrency -n $requests -g $output $url 

$gnuplot << PLOT
set terminal png
set output "${output}.png"
set title "ab -c $concurrency -n $requests $url"
set size 1,1
set grid y
set xlabel "Request"
set ylabel "Response Time (ms)"
plot "${output}" using 10 smooth sbezier with lines title "% served in time (ms)"
quit
PLOT

if test -x "$eog"; then
    $eog ${output}.png
fi
