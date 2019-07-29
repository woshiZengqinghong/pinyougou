var app = new Vue({
    el: "#app",
    data: {
       username:''

    },
    methods:{
        getUserInfo:function () {
            axios.get('/login/user/info.shtml').then(
                function (response) {
                    app.username=response.data;
                }
            )


        }

    },
    created:function () {
           this.getUserInfo();
    }
});