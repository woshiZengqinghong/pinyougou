var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        //页面加载的时候 发送请求 获取所有的品牌列表的数据  组合成 如下的格式  赋值给该变量
        brandOptions:[],
        //页面加载的时候发送请求获取 所有的规格的列表的数据  组合成 [id:1,text:'']
        specOptions:[],
        entity:{customAttributeItems:[]},
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/typeTemplate/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/typeTemplate/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/typeTemplate/findPage.shtml',{params:{
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
            axios.post('/typeTemplate/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/typeTemplate/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/typeTemplate/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;


/*
                // 将一个对象转成字符创
                JSON.stringify()
                //将一个字符串转成json对象
                JSON.parse()*/


                //将json字符串转成json对象(js对象)
                app.entity.specIds = JSON.parse(app.entity.specIds);
                app.entity.brandIds = JSON.parse(app.entity.brandIds);
                app.entity.customAttributeItems = JSON.parse(app.entity.customAttributeItems);

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/typeTemplate/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findAllBrandOptions:function () {
            axios.get('/brand/findAll.shtml').then(function (response) {
                // 要的  [{'id':1,'text':'联想'},{'id':2,'text':'华为'}]
               //response.data ======>[{id:1,name:"联想",firstChar:"L"},{}]//品牌的 列表

                var  brandList = response.data;

                for(var i=0;i<brandList.length;i++){
                    var obj = brandList[i];//   {id:1,name:"联想",firstChar:"L"}
                    app.brandOptions.push({"id":obj.id,"text":obj.name});
                }

            }).catch(function (error) {

            })
        },
        findAllSpecOptions:function () {
            axios.get('/specification/findAll.shtml').then(function (response) {
                // 要的  [{'id':1,'text':'颜色'},{'id':2,'text':'尺寸'}]
                //response.data ======>[{id:1,specName:"颜色"}]//规格的 列表

                var  specList = response.data;

                for(var i=0;i<specList.length;i++){
                    var obj = specList[i];//   {id:1,specName:"颜色"}
                    app.specOptions.push({"id":obj.id,"text":obj.specName});
                }

            }).catch(function (error) {

            })
        },
        addTableRow:function () {
            this.entity.customAttributeItems.push({});
        },
        removeTableRow:function (index) {
            this.entity.customAttributeItems.splice(index,1);
        },
        //将josn转成字符串以逗号拼接 返回
        jsonToString:function (strList,key) {
            var str  = "";

            //1.将json的字符串 转成 json的对象
            console.log(strList);
            //  [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            let jsonObjectArray = JSON.parse(strList);
            console.log(jsonObjectArray);

            for(let i=0;i<jsonObjectArray.length;i++){
                let obj = jsonObjectArray[i]; //{"id":27,"text":"网络"}
                str+=obj[key]+","
            }

            if(str.length>0){
                str =  str.substring(0,str.length-1);
            }


            return str;
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

        this.findAllBrandOptions();
        this.findAllSpecOptions();

    }

})
