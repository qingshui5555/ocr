<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>����OCR--By Lee</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <!--a
    <link rel="stylesheet" type="text/css" href="styles.css">
    -->

</head>
<script type="text/javascript">
    function check(){
        var path = document.getElementById("image").value;
        if(path.length==0){
            alert("��ѡ��Ҫ�����ͼƬ��");
            return false;
        }
        if(!(path.match(/.jpg$/i)||path.match(/.bmp$/i)||path.match(/.gif$/i)||path.match(/.png$/i))){
            alert("ֻ֧��JPG,BMP,GIF,PNG��ʽ��");
            return false;
        }
        return true;
    }
</script>
<body>
<form enctype="multipart/form-data" method="post" action="OCRServlet" onsubmit="return check();">
    ѡ���ļ���<input type="file" id="image" name="image"><br/>
    �ϴ��ļ���<input type="submit" value="�ύ�ϴ�">
</form>
<textarea rows="20" cols="60"><%Object txt = request.getAttribute("txt");
    if(txt!=null&&txt.toString().length()==0){
        out.print("δʶ����κ����֣�");
    }else if(txt!=null){
        out.print(txt.toString());
    }
%></textarea>
</body>
</html>
