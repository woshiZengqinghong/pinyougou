﻿var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        entity_1:{},
        entity_2:{},
        grade:1,//默认就是第一级
        ids:[],
        parentId:'',
        searchEntity:{}
    },

    methods: {
        searchList:function (curPage) {
            axios.post('/itemCat/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml',{params:{
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
            axios.post('/itemCat/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/itemCat/update.shtml',this.entity).then(function (response) {
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
            axios.get('/itemCat/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/itemCat/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findByParentId:function (parentId) {
            axios.get('/itemCat/findByParentId/'+parentId+'.shtml').then(function (response) {
                app.list=response.data;//lIST
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        selectList:function (p_entity) {
            //1.如果当前的等级是 1
            if(this.grade==1){
                this.entity_1={};
                this.entity_2={};
            }

            //2. 如果当前的等级是 2
            if(this.grade==2){
                this.entity_1=p_entity;
                this.entity_2={};
            }
            //3.如果当前的等级是 3
            if(this.grade==3){
                this.entity_2=p_entity;
            }

            this.findByParentId(p_entity.id);
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.findByParentId(0);

    }

})
