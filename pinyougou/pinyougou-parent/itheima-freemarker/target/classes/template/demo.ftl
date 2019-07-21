<html>
<head>
    <meta charset="utf-8">
    <title>Freemarker入门小DEMO </title>
</head>
<body>
<#include "head.ftl">
<br>
<#--我只是一个注释，我不会有任何输出  -->
${name},你好。${message}
<br>

<#assign linkman="周先生">
联系人：${linkman}
<br>

<#assign info={"mobile":"123321123456","address":"北京市昌平区王府街"}>
电话：${info.mobile}
地址：${info.address}
<br>

<#list goodsList as goods>
    ${goods_index+1}商品名称：${goods.name} 价格：${goods.price}<br>
</#list>

<#--共${goodsList?size} 条记录-->

<br>
<#--转换JSON字符串为对象-->
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank}
账号：${data.account}<br>

<#--日期格式化-->
当前日期：${today?date}<br>
当前时间：${today?time}<br>
当前时间日期：${today?datetime}<br>
日期格式化：${today?string("yyyy年MM月dd日 HH时mm分ss秒")}<br>

<#--数字去除千分位-->
${point?c};

<#if aaa??>
    aaa变量存在
<#else>
    aaa变量不存在
</#if>

${aaa!'-'}
</body>
</html>