var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        preDott:false,
        nextDott:false,
        searchMap:{keywords:"",category:"",brand:"",spec:{},price:"",pageNo:1,pageSize:40,sortField:"",sortType:""},//搜索的条件封装对象
        pageLabels:[],//页码存储的变量
        resultMap:{},//搜索的结果封装对象
        searchEntity:{},
    },
    methods: {
        isKeywordsIsBrand:function () {
            if (this.resultMap.brandList != null && this.resultMap.brandList.length > 0) {

                for (let i = 0; i <  this.resultMap.brandList.length; i++) {
                    if (this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text) != -1) {
                        this.searchMap.brand=this.resultMap.brandList[i].text;
                        return true;
                    }
                }
            }
            return false;
        },
        doSort:function(sortField,sortType){
            this.searchMap.sortField=sortField;
            this.searchMap.sortType=sortType;
            this.searchList();
        },
        clear:function () {
            this.searchMap={keywords:this.searchMap.keywords,category:"",brand:"",spec: {},pageNo:1,pageSize:40,sortField:"",sortType:""}

        },
        queryByPage:function (pageNo) {
             pageNo = parseInt(pageNo);
             this.searchMap.pageNo=pageNo;
             this.searchList();
        },
        buildPageLabel:function () {
            this.pageLabels=[];
            //显示以当前页为中心的5个页码
            let firstPage =1;
            let lastPage =this.resultMap.totalPages;//总页数

            if (this.resultMap.totalPages > 5) {
                //判断 如果当前的页码 小于等于3 pageNo<=3
                if (this.searchMap.pageNo <= 3) {
                    firstPage=1;
                    lastPage=5;
                    this.preDott=false;
                    this.nextDott=true;
                }else if(this.searchMap.pageNo>=this.resultMap.totalPages-2){//如果当前的页码大于= 总页数-2    98 99 100
                    firstPage=this.resultMap.totalPages-4;
                    lastPage=this.resultMap.totalPages;
                    this.preDott=true;
                    this.nextDott=false;
                }else{
                    firstPage=this.resultMap.pageNo-2;
                    lastPage=this.searchMap.pageNo+2;
                    this.preDott=true;
                    this.nextDott=true;
                }

            }else{
                this.preDott=false;
                this.nextDott=false;

            }


            for (let i = firstPage; i <= lastPage; i++) {
                this.pageLabels.push(i);
            }

        },
        removeSearchItem:function (key) {
            if("category"==key || "brand"==key || "price"==key){
                this.searchMap[key]="";
            }else{
                // console.log(key);
                delete this.searchMap.spec[key];
                // this.searchMap.spec[key]="";
            }
            this.searchList();
        },
        addSearchItem:function (key,value) {
            if("category"==key || "brand"==key || "price"==key){

                this.searchMap[key]=value;
            }else{
                this.searchMap.spec[key]=value;
            }
            this.searchList();
        },
        searchList:function () {

            axios.post("/itemSearch/search.shtml",this.searchMap).then(function (response) {
                //获取数据
                app.resultMap=response.data;
                //调用方法重新构建分页标签
                app.buildPageLabel();
                //默认获取第一个值
                console.log(response.data);
            });
        },

         findPage:function () {
            var that = this;
            axios.get('/item/findPage.shtml',{params:{
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
        findOne:function (id) {
            axios.get('/item/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

    },
    //钩子函数 初始化了事件和
    created: function () {
        var urlParamObj = this.getUrlParam();

        if (urlParamObj.keywords != undefined && urlParamObj.keywords != null) {
            this.searchMap.keywords=decodeURIComponent(urlParamObj.keywords);            this.searchList();
            this.searchList();
        }

    }

});
