<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:output method="html" version="4.0" indent="yes"/>
 <xsl:preserve-space elements="*"/>

 <!--            -->
 <!-- parameters -->
 <!--            -->

 <xsl:param name="Product">Ant Pretty Build</xsl:param>
 <xsl:param name="VersionMajor">2</xsl:param>
 <xsl:param name="VersionMinor">1</xsl:param>
 <xsl:param name="VersionBuild">0</xsl:param>
 <xsl:param name="BuildDate">2005-02-14</xsl:param>
 <xsl:param name="License">Apache License Version 2.0</xsl:param>
 <xsl:param name="Author">Charbel BITAR</xsl:param>
 <xsl:param name="Copyright">&#169; 2003-2005</xsl:param>
 <xsl:param name="Website">http://antprettybuild.free.fr</xsl:param>
 <xsl:param name="Compatibility">All Ant versions</xsl:param>
 <xsl:param name="Usage">
  Just add in the buildfile, the reference to this stylesheet:
  <?xml-stylesheet type="text/xsl" href="antprettybuild.xsl"?>
  and preview it in browser.
 </xsl:param>

 <!-- environment variables if needed -->
 <!-- <xsl:param name="JAVA_HOME">C:\Program Files\Java\jdk1.5.0</xsl:param> -->
 <!-- <xsl:param name="ANT_HOME">C:\Program Files\Apache\Ant</xsl:param> -->
 <!-- <xsl:param name="PATH">%PATH%;C:\Program Files\Apache\Ant\bin</xsl:param> -->
 <xsl:param name="JAVA_HOME"></xsl:param>
 <xsl:param name="ANT_HOME"></xsl:param>
 <xsl:param name="PATH"></xsl:param>

 <!-- current xsl stylesheet -->
 <xsl:param name="xsl">
  <xsl:call-template name="me">
   <xsl:with-param name="s"><xsl:value-of select="concat(substring(substring-before(translate(substring-after(processing-instruction('xml-stylesheet'),'href='),'\','/'),'.xsl'),2),'.xsl')"/></xsl:with-param>
  </xsl:call-template>
 </xsl:param>

 <!-- base -->
 <xsl:param name="base"><xsl:value-of select="substring(substring-before(translate(substring-after(processing-instruction('xml-stylesheet'),'href='),'\','/'),$xsl),2)"/></xsl:param>

 <!-- external css and js files if needed -->
 <!-- <xsl:param name="css"><xsl:value-of select="$base"/>extstyle.css</xsl:param> -->
 <!-- <xsl:param name="js"><xsl:value-of select="$base"/>extscript.js</xsl:param> -->
 <xsl:param name="css"></xsl:param>
 <xsl:param name="js"></xsl:param>

 <!-- show -->
 <xsl:param name="showproperties">true</xsl:param>
 <xsl:param name="showtargets">true</xsl:param>
 <xsl:param name="showrunant">true</xsl:param>
 <xsl:param name="showsource">false</xsl:param>

 <!-- properties -->
 <xsl:param name="sortproperties">false</xsl:param>
 <xsl:param name="showpropertydescription">false</xsl:param>
 <xsl:param name="showtargetproperties">true</xsl:param>
 <xsl:param name="allowmodifyproperties">true</xsl:param>
 <xsl:param name="allowaddproperties">true</xsl:param>

 <!-- targets -->
 <xsl:param name="sorttargets">false</xsl:param>
 <xsl:param name="showsubtargets">true</xsl:param>
 <xsl:param name="showtargetdepends">true</xsl:param>
 <xsl:param name="allowruntargets">true</xsl:param>

 <!-- runant -->
 <!-- showlibblock -->
 <xsl:param name="showlibblock">false</xsl:param>
 <!-- showmoreblock -->
 <xsl:param name="showmoreblock">false</xsl:param>
 <!-- showlogblock -->
 <xsl:param name="showlogblock">true</xsl:param>
 <!-- useantlauncher (ANT_HOME must be defined) -->
 <xsl:param name="useantlauncher">false</xsl:param>
 <!-- windowstyle (0-10) -->
 <xsl:param name="windowstyle">0</xsl:param>
 <!-- autorun -->
 <xsl:param name="autorun">false</xsl:param>
 <!-- autoclose -->
 <xsl:param name="autoclose">false</xsl:param>
 <!-- endbuildalert -->
 <!--<xsl:param name="endbuildalert">Build completed.</xsl:param>-->
 <xsl:param name="endbuildalert"></xsl:param>
 <!-- startbuildsound -->
 <!-- <xsl:param name="startbuildsound"><xsl:value-of select="$base"/>startbuild.wav</xsl:param> -->
 <xsl:param name="startbuildsound"></xsl:param>
 <!-- endbuildsound -->
 <xsl:param name="endbuildsound"></xsl:param>

 <!--           -->
 <!-- templates -->
 <!--           -->

 <!-- / -->
 <xsl:template match="/">
  <html>
   <xsl:call-template name="head"/>
   <xsl:call-template name="body"/>
  </html>
 </xsl:template>

 <!-- head -->
 <xsl:template name="head">
  <head>
   <xsl:call-template name="title"/>
   <xsl:call-template name="style"/>
   <xsl:call-template name="extstyle"/>
   <xsl:call-template name="script"/>
   <xsl:call-template name="extscript"/>
  </head>
 </xsl:template>

 <!-- title -->
 <xsl:template name="title">
  <title><xsl:value-of select="/project/@name"/> - <xsl:value-of select="$Product"/></title>
 </xsl:template>

 <!-- style -->
 <xsl:template name="style">
  <style type="text/css">
   body {background-color:#003366; color:#ffffff}
   input.propertynamestyle {border:0px; background-color:#003366; color:#ffffff; font-weight:bold}
   input.propertyvaluestyle {border:0px; background-color:#003366; color:#ffffff}
   table.propertytablestyle {background-color:#003366}
   tr.propertytabletrstyle {background-color:#336699}
   table.targettablestyle {background-color:#003366}
   tr.targettabletrstyle {background-color:#336699}
   font.projectdescriptionstyle {color:#ffffcc}
   font.propertynamestyle {border:0px; background-color:#003366; color:#ffffff; font-weight:bold}
   font.propertydescriptionstyle {color:#ffffcc}
   font.resourcestyle {color:#ffffcc; font-weight:bold}
   font.urlstyle {color:#ffffcc; font-weight:bold}
   font.defaulttargetstyle {color:#ff0000; font-weight:bold}
   font.maintargetstyle {color:#ffffcc; font-weight:bold}
   font.maintargetdescriptionstyle {color:#ffffcc}
   font.subtargetstyle {color:#ffffff; font-weight:bold}
   font.targetdependsstyle {color:#ffffff}
  </style>
 </xsl:template>

 <!-- extstyle -->
 <xsl:template name="extstyle">
  <xsl:if test="$css">
   <link rel="stylesheet" type="text/css" href="{$css}"/>
  </xsl:if>
 </xsl:template>

 <!-- script -->
 <xsl:template name="script">
  <script language="javascript" type="text/javascript">
   <![CDATA[
   //
   var targetline = '';
   var targetorder = 0;
   var propertyline = '';
   var iteration = 0;
   var nit = 0;
   var buildurl = window.location.pathname.substring(1);
   var buildfile = buildurl.replace(new RegExp('(%20)','g'),' ');
   var builddir = buildfile.substring(0,buildfile.lastIndexOf('\\'));
   var xmldoc = new ActiveXObject('Microsoft.XMLDOM');
   xmldoc.async = false;
   xmldoc.load(buildurl);
   var propertylist = xmldoc.getElementsByTagName('property');
   var targetlist = xmldoc.getElementsByTagName('target');
   if (xmldoc.getElementsByTagName('project').item(0).attributes.getNamedItem('basedir')) {
    var basedir = xmldoc.getElementsByTagName('project').item(0).attributes.getNamedItem('basedir').nodeValue;
   }
   else {
    var basedir = '';
   }
   if (xmldoc.getElementsByTagName('project').item(0).attributes.getNamedItem('name')) {
    var projectname = xmldoc.getElementsByTagName('project').item(0).attributes.getNamedItem('name').nodeValue;
   }
   else {
    var projectname = '';
   }

   //
   function EC(TheTR) {
    var DataTR = eval('document.all.'+TheTR);
    if (DataTR.style.display=='block' || DataTR.style.display=='') {
     DataTR.style.display = 'none';
    }
    else {
     DataTR.style.display = 'block';
    }
   }

   //
   function changeproperty(p) {
    var pattern = ' -D' + p.name.substring(p.name.indexOf('property_')+9)+'='+'\"';
    var reg = new RegExp(pattern,'g');
    if (propertyline.indexOf(pattern)!=-1) {
     reg = propertyline.substring(propertyline.indexOf(pattern),propertyline.indexOf('\"',propertyline.indexOf(pattern)+pattern.length)+1);
     propertyline = propertyline.replace(reg,'');
    }
    propertyline += ' -D' + p.name.substring(p.name.indexOf('property_')+9) + '=' +'\"' + p.value + '\"';
    for (var i=0;i<propertylist.length;i++) {
     if (propertylist.item(i).attributes.getNamedItem('name') && propertylist.item(i).attributes.getNamedItem('name').nodeValue==p.name.substring(p.name.indexOf('property_')+9)) {
      if (propertylist.item(i).attributes.getNamedItem('value')) propertylist.item(i).attributes.getNamedItem('value').nodeValue = p.value;
      if (propertylist.item(i).attributes.getNamedItem('location')) propertylist.item(i).attributes.getNamedItem('location').nodeValue = p.value;
     }
    }
   }

   //
   function changepropertyfile(p) {
    var pattern = ' -propertyfile' + ' ' + '\"';
    var reg = new RegExp(pattern,'g');
    if (propertyline.indexOf(pattern)!=-1) {
     reg = propertyline.substring(propertyline.indexOf(pattern),propertyline.indexOf('\"',propertyline.indexOf(pattern)+pattern.length)+1);
     propertyline = propertyline.replace(reg,'');
    }
    propertyline += ' -propertyfile' + ' ' + '\"' + p.value + '\"';
   }

   //
   function expandproperty(pvalue) {
    var nvalue,vvalue,pattern,reg;
    for (var i=0;i<propertylist.length;i++) {
     if (propertylist.item(i).attributes.getNamedItem('name')) {
      nvalue = propertylist.item(i).attributes.getNamedItem('name').nodeValue;
      if (propertylist.item(i).attributes.getNamedItem('value')) vvalue = propertylist.item(i).attributes.getNamedItem('value').nodeValue;
      if (propertylist.item(i).attributes.getNamedItem('location')) vvalue = propertylist.item(i).attributes.getNamedItem('location').nodeValue;
     }
     pattern = '\\$\\{' + nvalue + '}';
     reg = new RegExp(pattern,'g');
     pvalue = pvalue.replace(reg,vvalue);
     pattern = '\\$\\{' + 'basedir' + '}';
     reg = new RegExp(pattern,'g');
     pvalue = pvalue.replace(reg,basedir);
     pattern = '\\$\\{' + 'ant.file' + '}';
     reg = new RegExp(pattern,'g');
     pvalue = pvalue.replace(reg,buildfile);
     pattern = '\\$\\{' + 'ant.project.name' + '}';
     reg = new RegExp(pattern,'g');
     pvalue = pvalue.replace(reg,projectname);
    }
    if (pvalue.indexOf('${')!=-1 && nit<10) {
     nit++;
     pvalue = expandproperty(pvalue);
     nit = 0;
    }
    return(pvalue);
   }

   //
   function addproperty() {
    var ptable = document.getElementById('propertytable');
    var row = ptable.insertRow(ptable.rows.length);
    iteration++;
    row.name = 'custompropertyrow' + iteration;
    row.id = row.name;
    // first cell
    var cell1 = row.insertCell();
    var el1 = document.createElement('input');
    el1.type = 'text';
    el1.name = 'custompropertyname' + iteration;
    el1.id = el1.name;
    el1.value = '';
    el1.size = 30;
    el1.className = 'propertynamestyle';
    cell1.appendChild(el1);
    // second cell
    var cell2 = row.insertCell();
    var el2 = document.createElement('input');
    el2.type = 'text';
    el2.name = 'custompropertyvalue' + iteration;
    el2.id = el2.name;
    el2.value = '';
    el2.size = 30;
    el2.className = 'propertyvaluestyle';
    cell2.appendChild(el2);
    // third cell
    try {
     if (ptable.rows[0].cells.length>3) {
      var cell3 = row.insertCell();
      var el3 = document.createTextNode(' ');
      cell3.appendChild(el3);
     }
    }
    catch(e) {
     return 0;
    }
    // fourth cell
    var cell4 = row.insertCell();
    var el4 = document.createElement('input');
    el4.type = 'button';
    el4.name = 'custompropertyremovebutton' + iteration;
    el4.id = el4.name;
    el4.value = 'x';
    el4.title = 'Remove';
    el4.onclick = function(){removeproperty(row.name);};
    cell4.appendChild(el4);
   }

   //
   function removeproperty(prow) {
    document.getElementById('propertytable').deleteRow(document.getElementById(prow).rowIndex);
   }

   //
   function checkornot(targetbox) {
    var targetbutton = document.getElementById('targetbutton_'+targetbox.name.substring(targetbox.name.indexOf('targetbox_')+10));
    if (targetbox.checked) {
     targetline += ' ' + targetbox.name.substring(targetbox.name.indexOf('targetbox_')+10);
     targetorder++;
     targetbutton.value = targetorder;
    }
    else {
     var pattern = ' ' + targetbox.name.substring(targetbox.name.indexOf('targetbox_')+10);
     var reg = new RegExp(pattern,'g');
     targetline = targetline.replace(reg,'');
     for (var i=0;i<document.targetform.elements.length;i++) {
      var e = document.targetform.elements[i];
      if (e.type=='button' && parseInt(e.value)>parseInt(targetbutton.value)) {
       e.value--;
      }
     }
     targetorder--;
     targetbutton.value = '';
    }
   }

   //
   function viewsource() {
    try {
     var fso = new ActiveXObject('Scripting.FileSystemObject');
     var tempdir = fso.GetSpecialFolder(2);
     var tempfile = tempdir + '\\' + 'source.xml';
     var f = fso.OpenTextFile(tempfile,2,true);
     f.Write(xmldoc.documentElement.xml);
     f.Close();
     window.open(tempfile,'sourcewin','height=400px,width=600px,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,status=no,directories=no');
     if (fso.FileExists(tempfile)) fso.DeleteFile(tempfile,true);
    }
    catch(e) {
    }
   }

   //
   function viewtarget(tname) {
    try {
     for (var i=0;i<targetlist.length;i++) {
      if (targetlist.item(i).attributes.getNamedItem('name').nodeValue==tname.substring(tname.indexOf('target_')+7)) {
       var fso = new ActiveXObject('Scripting.FileSystemObject');
       var tempdir = fso.GetSpecialFolder(2);
       var tempfile = tempdir + '\\' + 'target.xml';
       var f = fso.OpenTextFile(tempfile,2,true);
       f.Write(targetlist.item(i).xml);
       f.Close();
       window.open(tempfile,'targetwin','height=200px,width=300px,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,status=no,directories=no');
       if (fso.FileExists(tempfile)) fso.DeleteFile(tempfile,true);
      }
     }
    }
    catch(e) {
    }
   }

   //
   function runant(java_home,ant_home,path,targetline,useantlauncher,windowstyle,endbuildalert) {
    // environment variables
    var setenvvars = '';
    var slash = new RegExp('(/)','g');
    java_home = java_home.replace(slash,'\\');
    ant_home = ant_home.replace(slash,'\\');
    path = path.replace(slash,'\\');
    if (java_home!='') setenvvars += '&' + 'set JAVA_HOME=' + java_home;
    if (ant_home!='') setenvvars += '&' + 'set ANT_HOME=' + ant_home;
    if (path!='') setenvvars += '&' + 'set PATH=' + path;
    // liblist
    var liblist = '';
    if (document.getElementById('lib')!=null) {
     var liblist = document.runform.lib.value;
     if (liblist!='') liblist = '-lib ' + '\"' + liblist + '\"';
    }
    // more commandline options
    var more = '';
    if (document.getElementById('more')!=null) {
     var more = document.runform.more.value;
     if (more!='') more = document.runform.more.value;
    }
    // logger
    var logger = '';
    if (document.getElementById('loggerselect')!=null) {
     var logger = document.runform.loggerselect.value;
     //if (logger=='') logger = '-logger ' + 'org.apache.tools.ant.DefaultLogger';
     if (logger=='xmllogger') logger = '-logger ' + 'org.apache.tools.ant.XmlLogger' + ' -D' + 'ant.XmlLogger.stylesheet.uri=' + '\"' + ant_home + '\\etc\\log.xsl' + '\"';
     // logfile
     var logfile = document.runform.logfileinput.value;
     if (logfile!='') logfile = '-logfile ' + '\"' + logfile + '\"';
    }
    // runmode
    var runmode = document.runform.modeselect.value;
    if (runmode!='') runmode = '-' + runmode;
    // custompropertyline
    var custompropertyline = '';
    for (var i=0;i<document.propertyform.elements.length;i++) {
     var e = document.propertyform.elements[i];
     if (e.type=='text' && e.name.indexOf('custompropertyname')!=-1 && e.value!='') {
      var pname = e.value;
      var pvalue = document.getElementById('custompropertyvalue' + e.name.substring(18)).value;
      custompropertyline += ' -D' + pname + '=' + '\"' + pvalue + '\"';
     }
    }
    // keep or close when done
    var korc = '/c';
    if (!document.runform.closewhendone.checked) {
     korc = '/k';
     windowstyle = 1;
    }
    // commandline
    // use javaw.exe and ant-launcher.jar if ant_home is defined (since Ant 1.6).
    // This avoid the black command prompt box popping up ;-)
    if (useantlauncher=='true' && ant_home!='') {
     var commandline = 'javaw.exe' + ' ' + '-cp' + ' ' + '\"' + ant_home + '\\lib\\ant-launcher.jar' + '\"'
        + ' ' + 'org.apache.tools.ant.launch.Launcher' + ' ' + '-buildfile' + ' ' + '\"' + buildfile + '\"'
        + ' ' + liblist + ' ' + targetline + ' ' + runmode + ' ' + logger + ' ' + logfile
        + ' ' + propertyline + ' ' + custompropertyline + ' ' + more;
    }
    else
    {
     var commandline = '%comspec%' + ' ' + korc + ' ' + 'cd ' + '\"' + builddir + '\"' + setenvvars
        + '&' + 'ant' + ' ' + '-buildfile' + ' ' + '\"' + buildfile + '\"'
        + ' ' + liblist + ' ' + targetline + ' ' + runmode + ' ' + logger + ' ' + logfile
        + ' ' + propertyline + ' ' + custompropertyline + ' ' + more;
    }
    // WshShell
    var WshShell = new ActiveXObject('WScript.Shell');
    // set the working directory to builddir
    WshShell.CurrentDirectory = builddir;
    // startbuildsound
    if (document.getElementById('startbuildsound')!=null) {
     document.all.startbuildsound.play();
    }
    // if windowstyle=0 black command prompt box will be hidden
    WshShell.Run(commandline,windowstyle,true);
    WshShell.Quit;
    // endbuildsound
    if (document.getElementById('endbuildsound')!=null) {
     document.all.endbuildsound.play();
    }
    // endbuildalert
    if (endbuildalert!='') alert(endbuildalert);
   }
   ]]>
  </script>
 </xsl:template>

 <!-- extscript -->
 <xsl:template name="extscript">
  <xsl:if test="$js">
   <script language="javascript" type="text/javascript" src="{$js}"></script>
  </xsl:if>
 </xsl:template>

 <!-- body -->
 <xsl:template name="body">
  <body>
   <xsl:call-template name="header"/>
   <xsl:call-template name="main"/>
   <xsl:call-template name="footer"/>
  </body>
 </xsl:template>

 <!-- header -->
 <xsl:template name="header">
  <table width="100%" border="0" cellpadding="0" cellspacing="0" style="border-collapse:collapse; text-align:center">
   <tr>
    <td width="20%"><hr/></td>
    <td width="60%"><hr/></td>
    <td width="20%"><hr/></td>
   </tr>
   <tr>
    <td width="20%"><!-- ASCII Art by Conor MacNeill --><pre>\_/<br/>\(_)/<br/>-(_)-<br/>/(_)\</pre></td>
    <td width="60%"><xsl:call-template name="projects"/></td>
    <td width="20%"><!-- ASCII Art by Conor MacNeill --><pre>\_/<br/>\(_)/<br/>-(_)-<br/>/(_)\</pre></td>
   </tr>
   <tr>
    <td width="20%"><hr/></td>
    <td width="60%"><hr/></td>
    <td width="20%"><hr/></td>
   </tr>
  </table>
 </xsl:template>

 <!-- main -->
 <xsl:template name="main">
  <table width="100%" border="0" cellpadding="0" cellspacing="0" style="border-collapse:collapse; text-align:center">
   <xsl:if test="$showproperties='true'">
    <tr>
     <td><xsl:call-template name="properties"/></td>
    </tr>
    <tr>
     <td>&#160;</td>
    </tr>
   </xsl:if>
   <xsl:if test="$showtargets='true'">
    <tr>
     <td><xsl:call-template name="targets"/></td>
    </tr>
    <tr>
     <td>&#160;</td>
    </tr>
   </xsl:if>
   <xsl:if test="$showrunant='true'">
    <tr>
     <td><xsl:call-template name="runant"/></td>
    </tr>
    <tr>
     <td>&#160;</td>
    </tr>
   </xsl:if>
   <xsl:if test="$showsource='true'">
    <tr>
     <td><xsl:call-template name="source"/></td>
    </tr>
    <tr>
     <td>&#160;</td>
    </tr>
   </xsl:if>
   <!-- Touch me here -->
  </table>
 </xsl:template>

 <!-- footer -->
 <xsl:template name="footer">
  <table width="100%" border="0" cellpadding="0" cellspacing="0" style="border-collapse:collapse; text-align:center">
   <tr>
    <td><hr/></td>
   </tr>
   <tr>
    <td><h5><i>Powered by <xsl:value-of select="$Product"/>&#160;<xsl:value-of select="concat($VersionMajor,'.',$VersionMinor,'.',$VersionBuild)"/>, <xsl:value-of select="$Copyright"/>&#160;<xsl:value-of select="$Author"/>. All rights reserved.</i></h5></td>
   </tr>
  </table>
 </xsl:template>

 <!-- projects -->
 <xsl:template name="projects">
  <xsl:apply-templates select="project" mode="project"/>
 </xsl:template>

 <!-- properties -->
 <xsl:template name="properties">
  <table>
   <tbody>
    <tr style="cursor:hand" onclick="javascript:EC('Eproperties')">
     <th align="center" nowrap="nowrap" colspan="0"><b>Properties</b></th>
    </tr>
    <tr id="Eproperties" name="Eproperties" style="display:none">
     <td>
      <form id="propertyform" name="propertyform">
       <table id="propertytable" name="propertytable" border="1" cellspacing="0" class="propertytablestyle">
        <tr style="padding:3px" class="propertytabletrstyle">
         <th align="center" style="padding:3px" nowrap="nowrap">Property</th>
         <th align="center" style="padding:3px" nowrap="nowrap">Value</th>
         <xsl:if test="$showpropertydescription='true'">
          <th align="center" style="padding:3px" nowrap="nowrap">Description</th>
         </xsl:if>
         <xsl:if test="$allowmodifyproperties='true'">
          <th align="center" nowrap="nowrap"><input id="reset" name="reset" type="reset" value="0" title="Reset" onclick="javascript:propertyline=''"/></th>
         </xsl:if>
        </tr>
        <xsl:choose>
         <xsl:when test="$sortproperties='true'">
          <xsl:choose>
           <xsl:when test="$showtargetproperties='true'">
            <xsl:apply-templates select="project//property" mode="property">
             <xsl:sort select="@name"/>
            </xsl:apply-templates>
           </xsl:when>
           <xsl:otherwise>
            <xsl:apply-templates select="project/property" mode="property">
             <xsl:sort select="@name"/>
            </xsl:apply-templates>
           </xsl:otherwise>
          </xsl:choose>
         </xsl:when>
         <xsl:otherwise>
          <xsl:choose>
           <xsl:when test="$showtargetproperties='true'">
            <xsl:apply-templates select="project//property" mode="property"/>
           </xsl:when>
           <xsl:otherwise>
            <xsl:apply-templates select="project/property" mode="property"/>
           </xsl:otherwise>
          </xsl:choose>
         </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$allowmodifyproperties='true' and $allowaddproperties='true'">
         <tr>
          <td>&#160;</td>
          <td>&#160;</td>
          <xsl:if test="$showpropertydescription='true'">
           <td>&#160;</td>
          </xsl:if>
          <td><input id="addpropertybutton" name="addpropertybutton" type="button" value="+" title="Add" onclick="javascript:addproperty()"/></td>
         </tr>
        </xsl:if>
       </table>
      </form>
     </td>
    </tr>
   </tbody>
  </table>
 </xsl:template>

 <!-- targets -->
 <xsl:template name="targets">
  <table>
   <tbody>
    <tr style="cursor:hand" onclick="javascript:EC('Etargets')">
     <th align="center" nowrap="nowrap" colspan="0"><b>Targets</b></th>
    </tr>
    <tr id="Etargets" name="Etargets" style="display:none">
     <td>
      <form id="targetform" name="targetform">
       <table id="targettable" name="targettable" border="1" cellspacing="0" class="targettablestyle">
        <tr style="padding:3px" class="targettabletrstyle">
         <xsl:if test="$allowruntargets='true'">
          <th align="center" nowrap="nowrap">&#160;</th>
          <th align="center" nowrap="nowrap">&#160;</th>
         </xsl:if>
         <th align="center" style="padding:3px" nowrap="nowrap">Target</th>
         <th align="center" style="padding:3px" nowrap="nowrap">Description</th>
         <xsl:if test="$showtargetdepends='true'">
          <th align="center" style="padding:3px" nowrap="nowrap">Depends</th>
         </xsl:if>
        </tr>
        <xsl:choose>
         <xsl:when test="$sorttargets='true'">
          <xsl:apply-templates select="project/target" mode="target">
           <xsl:sort select="@name"/>
          </xsl:apply-templates>
         </xsl:when>
         <xsl:otherwise>
          <xsl:apply-templates select="project/target" mode="target"/>
         </xsl:otherwise>
        </xsl:choose>
       </table>
      </form>
     </td>
    </tr>
   </tbody>
  </table>
 </xsl:template>

 <!-- runant -->
 <xsl:template name="runant">
  <table>
   <tbody>
    <tr style="cursor:hand" onclick="javascript:EC('Erunant')">
     <th align="center" nowrap="nowrap" colspan="0"><b>Run Ant</b></th>
    </tr>
    <tr id="Erunant" name="Erunant" style="display:none">
     <td>
      <form id="runform" name="runform">
       <table id="runtable" name="runtable">
        <xsl:if test="$showlibblock='true'">
         <tr>
          <td><b>Lib:</b></td>
          <td>
           <input id="lib" name="lib" type="text" size="15" value=""/>
           &#160;
           <input id="browselib" name="browselib" type="file" style="display:none"/>
           <input id="libbrowse" name="libbrowse" type="button" value="~" title="Browse" onclick="javascript:browselib.click();lib.value=browselib.value"/>
          </td>
         </tr>
        </xsl:if>
        <xsl:if test="$showmoreblock='true'">
         <tr>
          <td><b>More:</b></td>
          <td><input id="more" name="more" type="text" size="15" value=""/></td>
         </tr>
        </xsl:if>
        <xsl:if test="$showlogblock='true'">
         <tr>
          <td><b>Logger:</b></td>
          <td>
           <select name="loggerselect">
            <option value="" selected="selected">Default</option>
            <option value="xmllogger">XmlLogger</option>
           </select>
          </td>
         </tr>
         <tr>
          <td><b>Log&#160;File:</b></td>
          <td>
           <input id="logfileinput" name="logfileinput" type="text" size="15" value=""/>
           &#160;
           <input id="logfileview" name="logfileview" type="button" value="?" title="View" onclick="javascript:window.open(document.runform.logfileinput.value,'logfilewin','toolbar=no,menubar=no,location=no,status=no,directories=no,resizable=yes,scrollbars=yes')"/>
          </td>
         </tr>
        </xsl:if>
        <tr>
         <td><b>Mode:</b></td>
         <td>
          <select id="modeselect" name="modeselect">
           <option value="" selected="selected">Default</option>
           <option value="quiet">Quiet</option>
           <option value="verbose">Verbose</option>
           <option value="debug">Debug</option>
           <option value="emacs">Emacs</option>
          </select>
          &#160;
          <input id="runantbutton" name="runantbutton" type="button" value="Run" title="Run" onclick="javascript:runant('{translate($JAVA_HOME,'\','/')}','{translate($ANT_HOME,'\','/')}','{translate($PATH,'\','/')}',targetline,'{$useantlauncher}','{$windowstyle}','{$endbuildalert}')"/>
         </td>
        </tr>
        <tr>
         <td colspan="2"><input id="closewhendone" name="closewhendone" type="checkbox" checked="checked"/>Close when done</td>
        </tr>
        <xsl:if test="$startbuildsound!=''">
         <embed id="startbuildsound" name="startbuildsound" src="{$startbuildsound}" loop="false" autostart="false" hidden="true"/>
        </xsl:if>
        <xsl:if test="$endbuildsound!=''">
         <embed id="endbuildsound" name="endbuildsound" src="{$endbuildsound}" loop="false" autostart="false" hidden="true"/>
        </xsl:if>
       </table>
      </form>
     </td>
    </tr>
   </tbody>
  </table>
  <xsl:if test="$autorun='true'">
   <script language="javascript" type="text/javascript">
    <![CDATA[
    document.all.runantbutton.click();
    ]]>
   </script>
  </xsl:if>
  <xsl:if test="$autoclose='true'">
   <script language="javascript" type="text/javascript">
    <![CDATA[
    window.close(opener=0);
    ]]>
   </script>
  </xsl:if>
 </xsl:template>

 <!-- source -->
 <xsl:template name="source">
  <table>
   <tbody>
    <tr style="cursor:hand" onclick="javascript:viewsource()">
     <th align="center" nowrap="nowrap"><b>Source</b></th>
    </tr>
   </tbody>
  </table>
 </xsl:template>

 <!--                -->
 <!-- build elements -->
 <!--                -->

 <!-- project -->
 <xsl:template name="project" match="project" mode="project">
  <p>
   <b>Project Name:&#160;</b><xsl:value-of select="@name"/>
  </p>
  <xsl:if test="description">
   <p>
    <b>Project Description:&#160;</b><br/>
    <font class="projectdescriptionstyle"><xsl:value-of select="description"/></font>
   </p>
  </xsl:if>
  <xsl:if test="@basedir">
   <p>
    <b>Project Basedir:&#160;</b><xsl:value-of select="@basedir"/>
   </p>
  </xsl:if>
  <p>
   <b>Default target:&#160;</b><font class="defaulttargetstyle"><xsl:value-of select="@default"/></font>
  </p>
 </xsl:template>

 <!-- property -->
 <xsl:template name="property" match="property" mode="property">
  <xsl:choose>
   <xsl:when test="@name">
    <tr>
     <td align="center" style="padding:3px" nowrap="nowrap">
      <!-- Uncomment to show target names for target properties
      <xsl:if test="$showtargetproperties='true'">
       <xsl:value-of select="../@name"/>&#160;=>&#160;
      </xsl:if>
      -->
      <font class="propertynamestyle"><xsl:value-of select="@name"/></font>
     </td>
     <xsl:if test="@value|@location|@refid">
      <td>
       <input id="property_{@name}" name="property_{@name}" type="text" size="30" value="{@value|@location|@refid}" class="propertyvaluestyle" onchange="javascript:changeproperty(this)" onmouseover="javascript:window.status=expandproperty(this.value)">
        <xsl:if test="$allowmodifyproperties!='true'">
         <xsl:attribute name="readonly">readonly</xsl:attribute>
        </xsl:if>
       </input>
      </td>
     </xsl:if>
     <xsl:if test="$showpropertydescription='true'">
      <xsl:choose>
       <xsl:when test="@description and @description!=''">
        <td><font class="propertydescriptionstyle"><xsl:value-of select="@description"/></font></td>
       </xsl:when>
       <xsl:otherwise>
        <td>&#160;</td>
       </xsl:otherwise>
      </xsl:choose>
     </xsl:if>
     <xsl:if test="$allowmodifyproperties='true'">
      <td>&#160;</td>
     </xsl:if>
    </tr>
   </xsl:when>
   <xsl:otherwise>
    <xsl:if test="@resource|@file">
     <tr>
      <td align="center" style="padding:3px; cursor:hand" nowrap="nowrap" title="View" onclick="javascript:window.open(expandproperty(resource_{position()}.value))"><font class="resourcestyle"><xsl:value-of select="name(@*[position()=1])"/></font></td>
      <td>
       <input id="resource_{position()}" name="resource_{position()}" type="text" size="30" value="{@resource|@file}" class="propertyvaluestyle" onchange="javascript:changepropertyfile(this)" onmouseover="javascript:window.status=expandproperty(this.value)">
        <xsl:if test="$allowmodifyproperties!='true'">
         <xsl:attribute name="readonly">readonly</xsl:attribute>
        </xsl:if>
       </input>
      </td>
      <xsl:if test="$showpropertydescription='true'">
       <xsl:choose>
        <xsl:when test="@description and @description!=''">
         <td><font class="propertydescriptionstyle"><xsl:value-of select="@description"/></font></td>
        </xsl:when>
        <xsl:otherwise>
         <td>&#160;</td>
        </xsl:otherwise>
       </xsl:choose>
      </xsl:if>
      <xsl:if test="$allowmodifyproperties='true'">
       <td>
        <input id="browseresource_{position()}" name="browseresource_{position()}" type="file" style="display:none"/>
        <input id="resourcebrowse_{position()}" name="resourcebrowse_{position()}" type="button" value="~" title="Browse" onclick="javascript:browseresource_{position()}.click();resource_{position()}.value=browseresource_{position()}.value;resource_{position()}.onchange()"/>
       </td>
      </xsl:if>
     </tr>
    </xsl:if>
    <xsl:if test="@url">
     <tr>
      <td align="center" style="padding:3px; cursor:hand" nowrap="nowrap" title="View" onclick="javascript:window.open(url_{position()}.value)"><font class="urlstyle"><xsl:value-of select="name(@*[position()=1])"/></font></td>
      <td>
       <input id="url_{position()}" name="url_{position()}" type="text" size="30" value="{@url}" class="propertyvaluestyle" onchange="javascript:changeproperty(this)" onmouseover="javascript:window.status=expandproperty(this.value)">
        <xsl:if test="$allowmodifyproperties!='true'">
         <xsl:attribute name="readonly">readonly</xsl:attribute>
        </xsl:if>
       </input>
      </td>
      <xsl:if test="$showpropertydescription='true'">
       <xsl:choose>
        <xsl:when test="@description and @description!=''">
         <td><font class="propertydescriptionstyle"><xsl:value-of select="@description"/></font></td>
        </xsl:when>
        <xsl:otherwise>
         <td>&#160;</td>
        </xsl:otherwise>
       </xsl:choose>
      </xsl:if>
      <xsl:if test="$allowmodifyproperties='true'">
       <td>&#160;</td>
      </xsl:if>
     </tr>
    </xsl:if>
    <xsl:if test="@environment|@classpath|@classpathref|@prefix">
     <tr>
      <td align="center" style="padding:3px" nowrap="nowrap"><font class="propertynamestyle"><xsl:value-of select="name(@*[position()=1])"/></font></td>
      <td>
       <input id="{name(@*[position()=1])}_{position()}" name="{name(@*[position()=1])}_{position()}" type="text" size="30" value="{@environment|@classpath|@classpathref|@prefix}" class="propertyvaluestyle" onchange="javascript:changeproperty(this)">
        <xsl:if test="$allowmodifyproperties!='true'">
         <xsl:attribute name="readonly">readonly</xsl:attribute>
        </xsl:if>
       </input>
      </td>
      <xsl:if test="$showpropertydescription='true'">
       <xsl:choose>
        <xsl:when test="@description and @description!=''">
         <td><font class="propertydescriptionstyle"><xsl:value-of select="@description"/></font></td>
        </xsl:when>
        <xsl:otherwise>
         <td>&#160;</td>
        </xsl:otherwise>
       </xsl:choose>
      </xsl:if>
      <xsl:if test="$allowmodifyproperties='true'">
       <td>&#160;</td>
      </xsl:if>
     </tr>
    </xsl:if>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <!-- target -->
 <xsl:template name="target" match="target" mode="target">
  <xsl:choose>
   <xsl:when test="$showsubtargets='true'">
    <tr>
     <xsl:if test="$allowruntargets='true'">
      <td><input id="targetbox_{@name}" name="targetbox_{@name}" type="checkbox" title="Select" onclick="javascript:checkornot(this)"/></td>
      <td><input id="targetbutton_{@name}" name="targetbutton_{@name}" type="button" title="Run" onclick="javascript:runant('{translate($JAVA_HOME,'\','/')}','{translate($ANT_HOME,'\','/')}','{translate($PATH,'\','/')}','{@name}','{$useantlauncher}','{$windowstyle}','{$endbuildalert}')"/></td>
     </xsl:if>
     <td align="center" style="padding:3px; cursor:hand" nowrap="nowrap" title="View" onclick="javascript:viewtarget('target_{@name}')">
      <xsl:choose>
       <xsl:when test="@name=/project/@default">
       <font class="defaulttargetstyle"><xsl:value-of select="@name"/></font>
       </xsl:when>
       <xsl:otherwise>
        <xsl:choose>
         <xsl:when test="@description and @description!=''">
          <font class="maintargetstyle"><xsl:value-of select="@name"/></font>
         </xsl:when>
         <xsl:otherwise>
          <font class="subtargetstyle"><xsl:value-of select="@name"/></font>
         </xsl:otherwise>
        </xsl:choose>
       </xsl:otherwise>
      </xsl:choose>
     </td>
     <td align="left" style="padding:3px" nowrap="nowrap">
      <xsl:choose>
       <xsl:when test="@description and @description!=''">
        <font class="maintargetdescriptionstyle"><xsl:value-of select="@description"/></font>
       </xsl:when>
       <xsl:otherwise>
        &#160;
       </xsl:otherwise>
      </xsl:choose>
     </td>
     <xsl:if test="$showtargetdepends='true'">
      <td>
       <xsl:choose>
        <xsl:when test="@depends">
         <font class="targetdependsstyle"><xsl:value-of select="@depends"/></font>
        </xsl:when>
        <xsl:otherwise>
         &#160;
        </xsl:otherwise>
       </xsl:choose>
      </td>
     </xsl:if>
    </tr>
   </xsl:when>
   <xsl:otherwise>
    <xsl:if test="@description and @description!=''">
     <tr>
      <xsl:if test="$allowruntargets='true'">
       <td><input id="targetbox_{@name}" name="targetbox_{@name}" type="checkbox" title="Select" onclick="javascript:checkornot(this)"/></td>
       <td><input id="targetbutton_{@name}" name="targetbutton_{@name}" type="button" title="Run" onclick="javascript:runant('{translate($JAVA_HOME,'\','/')}','{translate($ANT_HOME,'\','/')}','{translate($PATH,'\','/')}','{@name}','{$useantlauncher}','{$windowstyle}','{$endbuildalert}')"/></td>
      </xsl:if>
      <td align="center" style="padding:3px; cursor:hand" nowrap="nowrap" title="View" onclick="javascript:viewtarget('target_{@name}')">
       <xsl:choose>
        <xsl:when test="@name=/project/@default">
         <font class="defaulttargetstyle"><xsl:value-of select="@name"/></font>
        </xsl:when>
        <xsl:otherwise>
         <font class="maintargetstyle"><xsl:value-of select="@name"/></font>
        </xsl:otherwise>
       </xsl:choose>
      </td>
      <td align="left" style="padding:3px" nowrap="nowrap">
       <font class="maintargetdescriptionstyle"><xsl:value-of select="@description"/></font>
      </td>
      <xsl:if test="$showtargetdepends='true'">
       <td>
        <xsl:choose>
         <xsl:when test="@depends">
          <font class="targetdependsstyle"><xsl:value-of select="@depends"/></font>
         </xsl:when>
         <xsl:otherwise>
          &#160;
         </xsl:otherwise>
        </xsl:choose>
       </td>
      </xsl:if>
     </tr>
    </xsl:if>
   </xsl:otherwise>
  </xsl:choose>
</xsl:template>

 <!-- me -->
 <xsl:template name="me">
  <xsl:param name="s"/>
  <xsl:choose>
   <xsl:when test="substring-after($s,'/')">
    <xsl:call-template name="me">
     <xsl:with-param name="s"><xsl:value-of select="substring-after($s,'/')"/></xsl:with-param>
    </xsl:call-template>
   </xsl:when>
   <xsl:otherwise>
    <xsl:value-of select="$s"/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

</xsl:stylesheet>