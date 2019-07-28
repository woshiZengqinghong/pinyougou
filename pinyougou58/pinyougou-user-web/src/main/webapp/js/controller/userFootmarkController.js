var app = new Vue({
    el: '#app',
    data:{
        loginName:'',
        list:[]
    },
    methods: {
        findAll:function () {
            axios.get('/footmark/findAll.shtml').then(function (response) {
                app.list=response.data
            })
        },
        //获取用户名
        getName: function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.loginName = response.data;
            })
        }
    },
    created:function () {
        this.getName();
        this.findAll();
    }
})