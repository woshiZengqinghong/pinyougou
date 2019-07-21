var app= new Vue({
    el:"#app",
    data:{
        list:[],//数组  [{id,name,firstchar},{},{}]
        entity:{},//品牌对象
        pages:15,//总页数 初始化值为15
        pageNo:1,//当前页 初始值 1
        ids:[],//用于存储选中的品牌的ID 数组删除用的
        searchEntity:{}//绑定搜索的条件对象
    },
    methods:{

        searchList:function (curPage) {
            axios.post('/brand/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {

                app.list=   response.data.list;
                app.pageNo=curPage;
                app.pages=response.data.pages;
                searchEntity={};
            })
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/brand/findAll.shtml?pageNo='+curPage).then(function (value) {


                console.log(value);
                //注意：this 在axios中就不再是 vue实例了。
                app.list= value.data;
            }).catch(function (reason) {

            })
        },
        add:function () {
            axios.post('/brand/add.shtml',this.entity).then(function (response) {
            console.log(response.data.success)
            if(response.data.success){
                app.searchList(1);
            }
            })
        },
        update:function () {
            axios.post('/brand/update.shtml',this.entity).then(function (response) {
                if(response.data.success){
                    app.searchList(1);
                }
            })
        },
        sava:function () {
            if(this.entity.id!=null){
                this.update();
            }else {
            this.add();
            }
        },

        //当点击修改的时候 根据点击到的品牌的ID 发送请求 获取数据赋值给变量entity
findOne:function (id) {
    axios.get('/brand/findOne/'+id+'.shtml').then(function (response) {
        app.entity=response.data;
    })
},
        dle:function () {
            axios.post('/brand/delete.shtml',this.ids).then(function (response) {
                if (response.data.success) {
                    app.ids=[];
                    app.searchList(1)
                }else{
                    alert(response.data.message);
                }
            })
        }


    },
    //钩子函数
    created:function () {
        this.searchList(1);

    }
})