#! /bin/sh
#Process scala files
files=`find ../app/controllers ../app/framework ../app/models ../app/utils -type f`
#files=`find . -name "*.scala"`
for file in $files
do
  s4=`grep -E "^    (def|val)" $file|wc -l|sed 's/^[ ]*//g'`

  #indent=4space
  if [ $s4 -gt 0 ]; then
    awk -f ./replace_indent.awk $file
  #indent=2space
  else
    awk -f ./add_confidential.awk $file
  fi

  #overide the original file
  rm -f $file
  edt_file="${file}.awk.edt"
  mv $edt_file $file
done

#Process html files
files=`find ../app/views -type f`
for file in $files
do
  awk -f ./replace_indent.awk $file
  #overide the original file
  rm -f $file
  edt_file="${file}.awk.edt"
  mv $edt_file $file
done
