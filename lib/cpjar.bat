set alljarRoot=G:\cvs130\ascjar

FOR /F "usebackq skip=1" %%f IN ( LIBLIST ) DO copy %alljarRoot%\%%f .