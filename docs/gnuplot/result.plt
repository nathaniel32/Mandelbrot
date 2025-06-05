meine_datei=system("echo $FILE")
plot meine_datei using 1:2 with line linewidth 3 notitle
#
# set output 'result.svg'
# set terminal svg
# replot
# gthumb zeigt svg

