var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchMap:{'keywords':'', 'category':'', 'brand':'', spec:{}, 'price':'', 'pageNo':1, 'pageSize':40, 'sortField':'', 'sortType':''},//搜索的条件封装对象
        resultMap:{},//搜索的结果封装对象
        searchEntity:{},
        pageLabels:[],
        preDott:false,
        nextDott:false
    },
    methods: {
        isKeywordsIsBrand:function () {
            if(this.resultMap.brandList!=null && this.resultMap.brandList.length>0) {
                for(var i=0;i<this.resultMap.brandList.length;i++){
                    if(this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text)!=-1){
                        this.searchMap.brand=this.resultMap.brandList[i].text;
                        return true;N
                    }
                }
                return false;
            }
        },

        doSort:function(sortField,sortType){
            this.searchMap.sortField=sortField;
            this.searchMap.sortType=sortType;
            this.searchList();
        },

            clear:function () {
            this.searchMap={'keywords':this.searchMap.keywords, 'category':'', 'brand':'', spec:{}, 'price':'', 'pageNo':1,'pageSize':40, 'sortField':'', 'sortType':''};
        },

        queryByPage:function(pageNo){
                pageNo = parseInt(pageNo);
                if(pageNo<1){
                    pageNo=1;
                }
                if(pageNo>this.resultMap.totalPages){
                    pageNo=this.resultMap.totalPages;
                }
                this.searchMap.pageNo=pageNo;
                this.searchList();
            },

            buildPageLabel:function(){ //导航栏显示5页
                //初始化数据
                this.pageLabels=[];

                var totalPages=parseInt(this.resultMap.totalPages);
                var pageNo=parseInt(this.searchMap.pageNo);
                var firstPage=1;
                var lastPage=totalPages;
                //总页数小于6页
                if(totalPages<6){
                    this.preDott=false;
                    this.nextDott=false;
                }else{
                    if(pageNo<=3){                  //只显示前5页
                        firstPage=1;
                        lastPage=5;
                        this.preDott=false;
                        this.nextDott=true;
                    }else if(pageNo>totalPages-3){  //显示最后5页
                        firstPage=totalPages-4;
                        lastPage=totalPages;
                        this.preDott=true;
                        this.nextDott=false;
                    }else{                          //显示中间页
                        firstPage=pageNo-2;
                        lastPage=pageNo+2;
                        this.preDott=true;
                        this.nextDott=true;
                    }
                }
                //设置页码
                for (var i=firstPage; i<=lastPage; i++){
                    this.pageLabels.push(i);
                }
            },

            removeSearchItem:function(key){
                if (key == 'brand' || key == 'category' ||key=='price') {
                    this.searchMap[key]='';
                }else{
                    delete this.searchMap.spec[key];
                }
                this.searchList();
            },

            addSearchItem:function(key,value){
                if (key == 'category' || key=='brand' || key=='price') {
                    this.searchMap[key]=value;
                }else{
                    this.searchMap.spec[key]=value;
                }
                this.searchList();
            },

            searchList:function () {
            axios.post('/itemSearch/search.shtml',this.searchMap).then(function (response) {
                //获取数据
                app.resultMap=response.data;
                app.buildPageLabel();
                //默认获取第一个值
                console.log(response.data);
            });
        },
    },

    //钩子函数 初始化了事件和
    created: function () {
        var urlParamObj = this.getUrlParam()
        if (urlParamObj.keywords != undefined && urlParamObj != null) {
            this.searchMap.keywords = decodeURIComponent(urlParamObj.keywords);
            this.searchList();
        }
    }
})
