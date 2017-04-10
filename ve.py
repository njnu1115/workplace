#-*- coding:utf-8 -*-

#import xml.etree.ElementTree as ET
from xml.etree.ElementTree import ElementTree
import os
if not os.path.exists('output.xml'):
	open('output.xml', 'w').close() 

N = 1 # you can use one more loop to change N from 1 to 20
tree = ElementTree()
tree.parse('model0331.xml')
root = tree.getroot()
for node in root.iter('node'):
	if node.attrib.get('id').find('CatchmentCSS') > -1:
		for child1 in node:
			if child1.tag.find('parameter') > -1:
				if child1.attrib['name'].find('K') > -1:
					oldvalue = child1.attrib['value']
					newvalue = int(oldvalue) - 10*N #here need to change oldvalue from str to int
					child1.attrib["value"] = str(newvalue) # here need to change int to str

tree.write('output.xml')