var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{goods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]},itemList:[]},
        ids:[],
        specList:[],//规格的数据列表 格式：[{id:1,text:"网络",options:[{},{}]}]
        itemCat1List:[],//一级分类的列表 变量
        itemCat2List:[],//二级分类的列表 变量
        itemCat3List:[],//三级分类的列表 变量
        brandTextList:[],//品牌的列表
        image_entity:{color:'',url:''},
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/goods/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/goods/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            //添加商品（3个表）
            //通过kindEditer的方法 获取 html代码，赋值给变量introduction
            this.entity.goodsDesc.introduction=editor.html();
            axios.post('/goods/add.shtml',this.entity).then(function (response) {
                if(response.data.success){
                    alert("添加成功");
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/goods/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    window.location.href="goods.html";
                }else {
                    alert(response.data.message)
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.goods.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/goods/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
                //富文本
                editor.html(response.data.goodsDesc.introduction);
                // //customAttributeItems
                app.entity.goodsDesc.customAttributeItems=JSON.parse(app.entity.goodsDesc.customAttributeItems)
                // //itemImages
                app.entity.goodsDesc.itemImages=JSON.parse(app.entity.goodsDesc.itemImages)
                //specificationItems
                app.entity.goodsDesc.specificationItems=JSON.parse(app.entity.goodsDesc.specificationItems);
                //itemList
                for(var i=0;i<app.entity.itemList.length;i++){
                    var item = app.entity.itemList[i];
                    item.spec=JSON.parse(item.spec);
                }

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        isChecked:function (specName,specValue) {
            var obj = this.searchObjectByKey(this.entity.goodsDesc.specificationItems,'attributeName',specName);
            if (obj != null) {
              if (obj.attributeValue.indexOf(specValue)!=-1){
                  return true;
              }
            }
            return false;
        },

     /*   isChecked:function (specValue) {
           var arr = this.entity.goodsDesc.specificationItems
            for (var i=0;i<arr.length;i++){
               if (arr[i].attributeValue.indexOf(specValue)!=-1){
                   return true;
               }
            }
            return false;
        },*/

        dele:function () {
            axios.post('/goods/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //   <input type="text" name="usenrame"> ----->pojo  username
        //方法 是用于图片上传使用 点击上传的按钮的时候调用
        uploadFile:function () {
            //创建一个表单的对象
            var formData=new FormData();

            //添加字段    formData.append('file'           ==> <input type="file"  name="file" value="文件本身">
            //            file.files[0]    第一个file 指定的时候 标签中的id   后面的files[0] 表示获取 选中的第一张文件 对象。File
            formData.append('file', file.files[0]);

            axios({
                url: 'http://localhost:18089/upload/uploadFile.shtml',
                //数据  表单数据
                data: formData,
                method: 'post',
                //设置表单提交的数据类型
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials:true
            }).then(function (response) {
                //文件上传成功
                if(response.data.success){
                    console.log(response.data.message);
                    app.image_entity.url=response.data.message;
                }else{
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
        //向数组中添加 图片对象
        addImageEntity:function () {
            this.entity.goodsDesc.itemImages.push(this.image_entity);
        },
        //获取一级分类的类别的方法
        findItemCat1List:function () {
            axios.get('/itemCat/findByParentId/0.shtml').then(
                function (response) {
                    //获取列表数据
                    app.itemCat1List=response.data;
                }
            )
        },
        //函数的作用：是就是点击的时候调用  改变 变量specificationItems的值
        /**
         *
         * @param specName  网络
         * @param specValue  移动4G
         */
        updateChecked:function ($event,specName,specValue) {

            var obj = this.searchObjectByKey(this.entity.goodsDesc.specificationItems,'attributeName',specName);//{"attributeValue":["移动3G"],"attributeName":"网络"}

            //1.如果有对象 直接设置对象里面的属性值

            if(obj!=null){

                if($event.target.checked){
                     //如果 是勾选  添加数据
                    //向数组属性中添加规格的选项值
                    obj.attributeValue.push(specValue);
                }else{
                    //如果取消勾选  删除数据

                    obj.attributeValue.splice(obj.attributeValue.indexOf(specValue),1);

                    //如果 删除完了数据
                    if( obj.attributeValue.length==0){
                        this.entity.goodsDesc.specificationItems.splice(this.entity.goodsDesc.specificationItems.indexOf(obj),1);
                    }

                }




            }else{
                //2.如果没有对象  直接添加对象

                this.entity.goodsDesc.specificationItems.push(
                    {"attributeValue":[specValue],"attributeName":specName} );
            }
        },
        //作用 就是 从specificationItems变量中 根据 规格的名称来 查询 是否都有对象
        /**
         *
         * @param list  指定的是要搜索的对象数组
         * @param specName  要 找的属性的名称
         * @param key   要找的属性的名称 对应的值
         * @returns {*}
         */
        searchObjectByKey:function (list,specName,key) {
            /*[
                {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},
                {"attributeValue":["16G","32G"],"attributeName":"机身内存"}
            ]*/
            var specItems = list;

            for(var i=0;i<specItems.length;i++){
                var obj = specItems[i];//{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}
                if(key==obj[specName]){
                    return obj;
                }
            }

            return null;

        },
        //点击复选框的时候 调用生成 sku列表的的变量
        createList:function () {
             //1.定义初始化的值
            this.entity.itemList=[{'spec':{},'price':0,'num':0,'status':'0','isDefault':'0'}];

            //2.循环遍历 specificationItems

            /**
             * [
             {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},
             {"attributeValue":["16G","32G"],"attributeName":"机身内存"}
             ]
             *
             */
            var specificationItems=this.entity.goodsDesc.specificationItems;
            for(var i=0;i<specificationItems.length;i++){
                //3.获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表
                var obj = specificationItems[i]; //{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}
                this.entity.itemList=this.addColumn(this.entity.itemList,obj.attributeName,obj.attributeValue);
            }
        },

        //.获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表 方法
        /**
         *
         * @param list
         * @param columnName  网络
         * @param columnValue  [移动3G,移动4G]
         */
        addColumn: function (list, columnName, columnValue) {
            var newList=[];

            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i];//   {'spec':{},'price':0,'num':0,'status':'0','isDefault':'0'}

                for (var j = 0; j < columnValue.length; j++) {
                    var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                    var value = columnValue[j];//移动3G
                    newRow.spec[columnName] = value;  //{'spec':{"网络":"移动3G"},'price':0,'num':0,'status':'0','isDefault':'0'}
                    newList.push(newRow);
                }


            }

            return newList;
        }



    },
    //定义一个监听
    watch:{
        //监听变量：entity.goods.category1Id 的变化  触发 一个函数 发送请求 获取 一级分类的下的二级分类的列表
        'entity.goods.category1Id':function (newval,oldval) {
            if(newval!=undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(
                    function (response) {
                        //获取列表数据
                        app.itemCat2List = response.data;
                    }
                )
            }
        },
        //监听二级分类的id的变化  查询 二级分类下的三级分类的列表数据
        'entity.goods.category2Id':function (newval,oldval) {
            if(newval!=undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(
                    function (response) {
                        //获取列表数据 三级分类的列表
                        app.itemCat3List = response.data;
                    }
                )
            }
        },
        //监听三级分类的id的变化  查询 三级分类对象里面的模板的id 展示到页面
        'entity.goods.category3Id':function (newval,oldval) {
            if(newval!=undefined) {
                axios.get('/itemCat/findOne/' + newval + '.shtml').then(
                    function (response) {
                        //获取列表数据 三级分类的列表
                        // app.entity.goods.typeTemplateId = response.data.typeId;
                        //第一个参数：需要改变的值的对象变量
                        //第二个参数：需要赋值的属性名
                        //第三个参数：要赋予的值
                        app.$set(app.entity.goods,'typeTemplateId',response.data.typeId);
                        console.log( response.data.typeId);
                        console.log( app.entity.goods.typeTemplateId);

                    }
                )
            }
        },
        //监听模板的ID 的变化 查询该模板的对象，对象里面有品牌列表数据
        'entity.goods.typeTemplateId': function (newval, oldval) {
            if (newval != undefined) {

                axios.get('/typeTemplate/findOne/' + newval + '.shtml').then(
                    function (response) {

                        //获取到的是模板的对象
                        var typeTemplate = response.data;
                        //品牌的列表
                        app.brandTextList=JSON.parse(typeTemplate.brandIds);//[{"id":1,"text":"联想"}]

                        //获取模板对象中扩展属性的值  定一个变量
                        if(app.entity.goods.id==null) {
                            app.entity.goodsDesc.customAttributeItems = JSON.parse(typeTemplate.customAttributeItems);
                        }
                    }
                )
                
                //监听模板的变化 根据模板的ID 获取模板的规格的数据拼接成要的格式 
                
                axios.get('/typeTemplate/findSpecList/'+newval+'.shtml').then(
                    function (response) {
                        app.specList=response.data;
                    }
                )
                

            }
        }

    },

    //钩子函数 初始化了事件和
    created: function () {
        //获取url中的id
        var request = this.getUrlParam();
        this.findOne(request.id);
        this.findItemCat1List();
    }

});
