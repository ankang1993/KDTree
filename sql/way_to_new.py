# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

f = open("C:\\Users\\delluser\\桌面\\way.txt","r")
fn = open("C:\\Users\\delluser\\桌面\\way_new.txt","a")
lines = f.readlines()
id = 0
for line in lines:
	if line.startswith('id'):
		id = line[3:]
	else:
		line = line[:-1]
		fn.write(line + '|' + id)
f.close()
fn.close()