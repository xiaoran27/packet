
/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-8-21
* + 分析req/rsp的fields和structure的fields
*-----------------------------------------------------------------------------*
* V,xiaoran27,2007-8-22
* M tagReq/RspFields的错位问题
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ParseProtocol {
    private String file = null;


    public ParseProtocol(String file) {
        this.file = file;
    }

    public void getCodeGeneralParams(){
        final int max=256;
        String[] tags = new String[max];
        String[] tagReqFields = new String[max];
        String[] tagRspFields = new String[max];
        String[] descipts = new String[max];
        String[] tables = new String[max];
        String[] handles = new String[max];
        String[] depends = new String[max];
        String[] jspdoes = new String[max];
        String[] models = new String[max];
        String[] structs = new String[max];
        String[] structFields = new String[max];
        File f = new File(file);
        if (!f.exists() || !f.isFile()){
            System.out.println(file+" is not found or not file.");
            System.exit(-1);
        }

        try {
            final String tagFlag="operation";
            final String tableFlag="tables:";
            final String handleFlag="handles:";
            final String dependFlag="depends:";
            final String jspdoFlag="jspdoes:";
            final String modelFlag="models:";
            final String operationFlag="OPERATION";
            final String optionalFlag="OPTIONAL";
            final String defaultFlag="DEFAULT";
            final String __Flag="--";
            final String structureFlag="STRUCTURE";
            final int intervalMax=6;
            int i =0, ii=0;


            BufferedReader br = new BufferedReader(new FileReader(f) );
            List<String> oldLineList = new ArrayList<String>();
            String line = br.readLine();
            String oriline = null;
            boolean isFoundTable = false;
            boolean isFoundTag = false;
            int afterTableLineNo =0;

            boolean isFoundoptionalTag = false;
            boolean isFoundOpTag = false;
            int opTag = 0;  //{匹配

            boolean isFoundStructTag = false;
            int structTag = 0;  //{匹配

            int pos = -1;
            while( line != null){
                oriline = line.trim();

                line = line.replaceAll("\t", " ").trim();
                String tmp = line.toLowerCase();
                pos = tmp.indexOf(tableFlag);
                if (pos>=0){
                    isFoundTable = true;
                    int tablePosion = line.indexOf(tableFlag);
                    int commentPosion = line.indexOf(" --");
                    tables[i] = line.substring((tablePosion+tableFlag.length()),commentPosion<0 ? line.length() : commentPosion).replaceAll(" ","");
                    //System.out.println("tables[i]="+tables[i]);
                }

                if(!isFoundTable){
                    if(line.indexOf("--")>=0){
                        int startCommentPosion = line.indexOf("--");
                        int secondCommentPosion = line.substring(startCommentPosion).indexOf(" --");
                        oldLineList.add(line.substring((startCommentPosion+2),secondCommentPosion<0 ? line.length() : secondCommentPosion).replaceAll(" ",""));
                    }
                }

                if(isFoundTable &&afterTableLineNo<intervalMax ){

                    if(!line.startsWith("--") && line.indexOf("::")>0 && tmp.indexOf(tagFlag)>0){
                        isFoundTag = true;
                        tags[i] = line.substring(0,line.indexOf("::")).trim();
                    }
                    if(!isFoundTag && line.startsWith("--")){
                        if(line.indexOf(dependFlag)>0){
                            int startDependsPosion = line.indexOf(dependFlag);
                            int secondCommentPosion = line.substring(startDependsPosion+dependFlag.length()).indexOf(" --");
                            depends[i] = line.substring((startDependsPosion+dependFlag.length()),secondCommentPosion<0 ? line.length() : secondCommentPosion).replaceAll(" ","");
                        }
                        if(line.indexOf(jspdoFlag)>0){
                            int startJspdoesPosion = line.indexOf(jspdoFlag);
                            int secondCommentPosion = line.substring(startJspdoesPosion+jspdoFlag.length()).indexOf(" --");
                            jspdoes[i] = line.substring((startJspdoesPosion+jspdoFlag.length()),secondCommentPosion<0 ? line.length() : secondCommentPosion).replaceAll(" ","").toLowerCase();
                        }
                        if(line.indexOf(handleFlag)>0){
                            int startHandlesPosion = line.indexOf(handleFlag);
                            int secondCommentPosion = line.substring(startHandlesPosion+handleFlag.length()).indexOf(" --");
                            handles[i] = line.substring((startHandlesPosion+jspdoFlag.length()),secondCommentPosion<0 ? line.length() : secondCommentPosion).replaceAll(" ","").toLowerCase();
                        }
                        if(line.indexOf(modelFlag)>0){
                            int startModelsPosion = line.indexOf(modelFlag);
                            int secondCommentPosion = line.substring(startModelsPosion+modelFlag.length()).indexOf(" --");
                            models[i] = line.substring((startModelsPosion+modelFlag.length()),secondCommentPosion<0 ? line.length() : secondCommentPosion).replaceAll(" ","").toLowerCase();
                        }
                    }
                    //if(!line.startsWith("--")){
                        afterTableLineNo++;
                    //}

                }

                if(afterTableLineNo>= intervalMax || isFoundTag){

                    boolean flag = false;
                    for(int j=oldLineList.size()-1;j>=0&&j>oldLineList.size()-intervalMax-1;j--){

                        if(oldLineList.get(j).trim().length()>0){
                            descipts[i] =oldLineList.get(j).trim();
                            flag = true;
                            break;
                        }
                    }

                    if(!flag){
                        descipts[i] = "";
                    }
                    if(!isFoundTag){
                        i--;
                    }
                    i++;
                    isFoundTable = false;
                    isFoundTag = false;
                    afterTableLineNo =0;
                    oldLineList =new ArrayList<String>();
                }


                //获取tag的req/rsp的fields
                if (!isFoundOpTag){
                    isFoundOpTag = !oriline.startsWith("--") && oriline.indexOf(operationFlag)>0;
                }else{
                    if (opTag>5){
                        isFoundOpTag=false;
                        opTag = 0;
                    }

                    if (oriline.startsWith("{") || oriline.startsWith("}")){
                        opTag ++;
                    }else{
                        if (opTag==2 && !oriline.startsWith("--")){  //req
                            isFoundoptionalTag = oriline.indexOf(optionalFlag)>0;
                            pos = oriline.indexOf(" ");
                            if (pos<0){
                                pos = oriline.indexOf(optionalFlag);
                            }
                            if (pos<0){
                                pos = oriline.indexOf(defaultFlag);
                            }
                            if (pos<0){
                                pos = oriline.indexOf(",");
                            }
                            if (pos<0){
                                pos = oriline.indexOf(__Flag);
                            }
                            if (pos>0){
                                tagReqFields[i-1] = (null==tagReqFields[i-1]?"":tagReqFields[i-1]+",") + oriline.substring(0,pos).trim();
                            }else{
                                tagReqFields[i-1] = (null==tagReqFields[i-1]?"":tagReqFields[i-1]+",") + oriline;
                            }
                            tagReqFields[i-1] = tagReqFields[i-1] + ";"+(isFoundoptionalTag?"1":"0");
                        }

                        if (opTag==4 && !oriline.startsWith("--")){  //rsp
                            isFoundoptionalTag = oriline.indexOf(optionalFlag)>0;
                            pos = oriline.indexOf(" ");
                            if (pos<0){
                                pos = oriline.indexOf(optionalFlag);
                            }
                            if (pos<0){
                                pos = oriline.indexOf(defaultFlag);
                            }
                            if (pos<0){
                                pos = oriline.indexOf(",");
                            }
                            if (pos<0){
                                pos = oriline.indexOf(__Flag);
                            }
                            if (pos>0){
                                tagRspFields[i-1] = (null==tagRspFields[i-1]?"":tagRspFields[i-1]+",") + oriline.substring(0,pos).trim();
                            }else{
                                tagRspFields[i-1] = (null==tagRspFields[i-1]?"":tagRspFields[i-1]+",") + oriline;
                            }
                            tagRspFields[i-1] = tagRspFields[i-1] + ";"+(isFoundoptionalTag?"1":"0");
                        }
                    }

                }

                //获取structure及其fields
                if (!isFoundStructTag){
                    isFoundStructTag = !oriline.startsWith("--") && oriline.indexOf(structureFlag)>0;
                    if (isFoundStructTag){
                        structs[ii]=oriline.substring(0,oriline.indexOf("::=")).trim();
                    }
                }else{
                    if (structTag>1){
                        isFoundStructTag=false;
                        structTag = 0;
                        ii++;
                    }

                    if (oriline.startsWith("{") || oriline.startsWith("}")){
                        structTag ++;
                    }else{
                        if (structTag==1 && !oriline.startsWith("--")){  //fields
                            isFoundoptionalTag = oriline.indexOf(optionalFlag)>0;
                            pos = oriline.indexOf(" ");
                            if (pos<0){
                                pos = oriline.indexOf(optionalFlag);
                            }
                            if (pos<0){
                                pos = oriline.indexOf(defaultFlag);
                            }
                            if (pos<0){
                                 pos = oriline.indexOf(",");
                            }
                            if (pos<0){
                                pos = oriline.indexOf(__Flag);
                            }
                            if (pos>0){
                                structFields[ii] = (null==structFields[ii]?"":structFields[ii]+",") + oriline.substring(0,pos).trim();
                            }else{
                                structFields[ii] = (null==structFields[ii]?"":structFields[ii]+",") + oriline;
                            }
                            structFields[ii] = structFields[ii] + ";"+(isFoundoptionalTag?"1":"0");
                        }
                    }
                }

                line = br.readLine();
            }

            String tagsHead ="#set($Tags = [";
            String tagReqFieldsHead ="#set($TagReqFields = [";
            String tagRspFieldsHead ="#set($TagRspFields = [";
            String descriptsHead ="#set($Descripts = [";
            String handlesHead ="#set($Handles = [";
            String tablesHead ="#set($Tables = [";
            String dependsHead ="#set($Depends = [";
            String jspdoesHead ="#set($Jspdoes = [";
            String modelsHead ="#set($Models = [";
            String structsHead ="#set($Structs = [";
            String structFieldsHead ="#set($StructFields = [";

            for(int j=0;j<256;j++){
                if(tagReqFields[j]==null){
                    tagReqFields[j] = "";
                }
                if(tagRspFields[j]==null){
                    tagRspFields[j] = "";
                }
                if(depends[j]==null){
                    depends[j] = "";
                }
                if(jspdoes[j]==null){
                    jspdoes[j] = "";
                }
                if(handles[j]==null){
                    handles[j] = "";
                }
                if(models[j]==null){
                    models[j] = "";
                }
                if(models[j]==null){
                    models[j] = "";
                }
                if(j==i-1){
                    tagsHead += "\""+tags[j]+"\"";
                    tagReqFieldsHead += "\""+tagReqFields[j]+"\"";
                    tagRspFieldsHead += "\""+tagRspFields[j]+"\"";
                    descriptsHead += "\""+descipts[j]+"\"";
                    tablesHead += "\""+tables[j]+"\"";
                    dependsHead += "\""+depends[j]+"\"";
                    jspdoesHead += "\""+jspdoes[j]+"\"";
                    handlesHead += "\""+handles[j]+"\"";
                    modelsHead += "\""+models[j]+"\"";
                }else if(j<i-1){
                    tagsHead += "\""+tags[j]+"\",";
                    tagReqFieldsHead += "\""+tagReqFields[j]+"\",";
                    tagRspFieldsHead += "\""+tagRspFields[j]+"\",";
                    descriptsHead += "\""+descipts[j]+"\",";
                    tablesHead += "\""+tables[j]+"\",";
                    dependsHead += "\""+depends[j]+"\",";
                    jspdoesHead += "\""+jspdoes[j]+"\",";
                    handlesHead += "\""+handles[j]+"\",";
                    modelsHead += "\""+models[j]+"\",";
                }

                if(structs[j]==null){
                    structs[j] = "";
                }
                if(structFields[j]==null){
                    structFields[j] = "";
                }
                if(j==ii-1){
                    structsHead += "\""+structs[j]+"\"";
                    structFieldsHead += "\""+structFields[j]+"\"";
                }else if(j<ii-1){
                    structsHead += "\""+structs[j]+"\",";
                    structFieldsHead += "\""+structFields[j]+"\",";
                }
            }
            tagsHead += "])";
            tagReqFieldsHead += "])";
            tagRspFieldsHead += "])";
            descriptsHead += "])";
            tablesHead += "])";
            dependsHead += "])";
            jspdoesHead += "])";
            handlesHead += "])";
            modelsHead += "])";
            structsHead += "])";
            structFieldsHead += "])";

            System.out.println(tagsHead);
            System.out.println(tagReqFieldsHead);
            System.out.println(tagRspFieldsHead);
            System.out.println(descriptsHead);
            System.out.println(tablesHead);
            System.out.println(dependsHead);
            System.out.println(jspdoesHead);
            System.out.println(handlesHead);
            System.out.println(modelsHead);
            System.out.println(structsHead);
            System.out.println(structFieldsHead);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }




    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        ParseProtocol parseProtocol = new ParseProtocol(args[0]);
        parseProtocol.getCodeGeneralParams();
    }

}
