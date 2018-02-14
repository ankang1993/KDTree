# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

# -*- coding: utf-8 -*-
"""
Created on Mon Sep 18 10:50:04 2017

@author: AK
"""
import operator
out = open("C:\\Users\\delluser\\桌面\\map_sorted.txt","a")
with open("C:\\Users\\delluser\\桌面\\map_final.txt") as f:
	l = [line.split(',') for line in f]
l.sort(key=operator.itemgetter(3))
for k in l:
	out.write(k[0] + "," + k[1] + "," + k[2] + "," + k[3])
out.close()