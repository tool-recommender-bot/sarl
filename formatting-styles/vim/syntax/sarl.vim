" Vim syntax file
" Language: SARL
" Version: 0.7
" 
"  $Id$
" 
"  File is automatically generated by the Xtext language generator.
"  Do not change it.
" 
"  SARL is an general-purpose agent programming language.
"  More details on http://www.sarl.io
" 
"  Copyright (C) 2014-2018 the original authors or authors.
" 
"  Licensed under the Apache License, Version 2.0 (the "License");
"  you may not use this file except in compliance with the License.
"  You may obtain a copy of the License at
" 
"       http://www.apache.org/licenses/LICENSE-2.0
" 
"  Unless required by applicable law or agreed to in writing, software
"  distributed under the License is distributed on an "AS IS" BASIS,
"  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
"  See the License for the specific language governing permissions and
"  limitations under the License.
" 


" Quit when a syntax file was already loaded
if !exists("main_syntax")
  if exists("b:current_syntax")
    finish
  endif
  " we define it here so that included files can test for it
  let main_syntax='sarl'
  syn region sarlFold start="{" end="}" transparent fold
endif

let s:cpo_save = &cpo
set cpo&vim

" don't use standard HiLink, it will not work with included syntax files
if version < 508
  command! -nargs=+ SarlHiLink hi link <args>
else
  command! -nargs=+ SarlHiLink hi def link <args>
endif

" some characters that cannot be in a SARL program (outside a string)
syn match sarlError "[\\`]"

" comments
syn region  sarlComment start="/\*" end="\*/" contains=@Spell
syn match sarlCommentStar contained "^\s*\*[^/]"me=e-1
syn match sarlCommentStar contained "^\s*\*$"
syn match sarlLineComment "//.*" contains=@Spell
syn cluster sarlTop add=sarlComment,sarlLineComment
" match the special comment /**/
syn match sarlComment "/\*\*/"

" numerical constants
syn match sarlNumber "[0-9][0-9]*\.[0-9]\+([eE][0-9]\+)\?[fFdD]\?"
syn match sarlNumber "0[xX][0-9a-fA-F]\+"
syn match sarlNumber "[0-9]\+[lL]\?"
syn cluster sarlTop add=sarlNumber

" Strings constants
syn match sarlSpecialError contained "\\."
syn match sarlSpecialCharError contained "[^']"
syn match sarlSpecialChar contained +\\\([4-9]\d\|[0-3]\d\d\|["\\'ntbrf]\|u\x\{4\}\)+
syn region  sarlString start='"' end='"' contains=sarlSpecialChar,sarlSpecialError,@Spell
syn region  sarlString start="'" end="'" contains=sarlSpecialChar,sarlSpecialError,@Spell
syn cluster sarlTop add=sarlString

" annnotation
syn match sarlAnnotation "@[_a-zA-Z][_0-9a-zA-Z]*\([.$][_a-zA-Z][_0-9a-zA-Z]*\)*"
syn cluster sarlTop add=sarlAnnotation

" primitive types.
syn match sarlArrayDeclaration contained "\(\s*\[\s*\]\)*"
syn keyword sarlPrimitiveType boolean byte char double float int long short void nextgroup=sarlArrayDeclaration
syn cluster sarlTop add=sarlPrimitiveType

" keywords for the 'sarlLiteral' family.
syn keyword sarlLiteral false it null occurrence this true void
syn cluster sarlTop add=sarlLiteral

" keywords for the 'sarlSpecial' family.
syn keyword sarlSpecial import package
syn cluster sarlTop add=sarlSpecial

" keywords for the 'sarlTypeDeclaration' family.
syn keyword sarlTypeDeclaration agent annotation artifact behavior capacity class enum event interface skill space
syn cluster sarlTop add=sarlTypeDeclaration

" keywords for the 'sarlModifier' family.
syn keyword sarlModifier abstract def dispatch final native override private protected public static strictfp synchronized transient val var volatile
syn cluster sarlTop add=sarlModifier

" keywords for the 'sarlKeyword' family.
syn keyword sarlKeyword as assert assume break case catch continue default do else extends extension finally fires for if implements instanceof new on requires return super switch throw throws try typeof uses while with
syn cluster sarlTop add=sarlKeyword

" catch errors caused by wrong parenthesis
syn region sarlParenT transparent matchgroup=sarlParen  start="(" end=")" contains=@sarlTop,sarlParenT1
syn region sarlParenT1 transparent matchgroup=sarlParen1 start="(" end=")" contains=@sarlTop,sarlParenT2 contained
syn region sarlParenT2 transparent matchgroup=sarlParen2 start="(" end=")" contains=@sarlTop,sarlParenT contained
syn match sarlParenError ")"
" catch errors caused by wrong square parenthesis
syn region sarlParenT transparent matchgroup=sarlParen  start="\[" end="\]" contains=@sarlTop,sarlParenT1
syn region sarlParenT1 transparent matchgroup=sarlParen1 start="\[" end="\]" contains=@sarlTop,sarlParenT2 contained
syn region sarlParenT2 transparent matchgroup=sarlParen2 start="\[" end="\]" contains=@sarlTop,sarlParenT  contained
syn match sarlParenError "\]"

SarlHiLink sarlParenError sarlError

if !exists("sarl_minlines")
  let sarl_minlines = 10
endif
exec "syn sync ccomment sarlComment minlines=" . sarl_minlines

" The default highlighting.
SarlHiLink sarlComment Comment
SarlHiLink sarlNumber Constant
SarlHiLink sarlString Constant
SarlHiLink sarlTypeDeclaration Type
SarlHiLink sarlLineComment Comment
SarlHiLink sarlLiteral Identifier
SarlHiLink sarlKeyword Statement
SarlHiLink sarlAnnotation PreProc
SarlHiLink sarlArrayDeclaration Special
SarlHiLink sarlSpecial Special
SarlHiLink sarlPrimitiveType Special
SarlHiLink sarlModifier Statement

delcommand SarlHiLink

let b:current_syntax = "sarl"

if main_syntax == 'sarl'
  unlet main_syntax
endif

let b:spell_options="contained"
let &cpo = s:cpo_save
unlet s:cpo_save
