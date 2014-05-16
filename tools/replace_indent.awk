#! /usr/bin/awk -f
#
#
# Trim the spaces from the head
# in: str the string to trim
#
function trimLeft(str) {
   tmp_str = str;                          # save the string to a temporary variable
   sub(/^[ \t]+/, "", tmp_str);
   return tmp_str;
}

#
# Return space block.
#
function spaceBlock()  {
   return "                                                                                                  ";
   #for test
   #return "_____________________________________________________________________________________ ";
}

function rontechConfidential() {
  str = "/**\n";
  str = str " * RONTECH CONFIDENTIAL\n";
  str = str " * __________________\n";
  str = str " *\n";
  str = str " *  [2014] - SuZhou Rontech Co.,Ltd.(http://www.sz-rontech.com)\n";
  str = str " *  All Rights Reserved.\n";
  str = str " *\n";
  str = str " * NOTICE:  All information contained herein is, and remains\n";
  str = str " * the property of SuZhou Rontech Co.,Ltd. and its suppliers,\n";
  str = str " * if any.  The intellectual and technical concepts contained\n";
  str = str " * herein are proprietary to SuZhou Rontech Co.,Ltd.\n";
  str = str " * and its suppliers and may be covered by China and Foreign Patents,\n";
  str = str " * patents in process, and are protected by trade secret or copyright law.\n";
  str = str " * Dissemination of this information or reproduction of this material\n";
  str = str " * is strictly forbidden unless prior written permission is obtained\n";
  str = str " * from SuZhou Rontech Co.,Ltd..\n";
  str = str " */";
  return str;
}


#
# Cut the indent spaces to half.
# The new lines are dumped to a file with [awk.edt] extension.
#
{
  EDT_FILE = FILENAME".awk.edt";

  #add confidential description
  if (NR == 1 && substr($0, 1, 3) != "/**" && index(EDT_FILE, "html") == 0) {
    print rontechConfidential() > EDT_FILE;
  }

  #cut the spaces of head
  #when the sapces is over 3.
  tLine = trimLeft($0);
  slen = length($0) - length(tLine);
  spaces = "";
  if (slen > 3) {
    spaces = substr(spaceBlock(), 1, (slen + 1) / 2);
    print spaces trimLeft(tLine) >> EDT_FILE;
  } else {
    print $0 >> EDT_FILE;
  }
}

