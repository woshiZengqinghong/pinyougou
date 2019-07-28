var app = new Vue({
    el: "#app",
    data: {
        order:{paymentType:''},
        loginName:'',
        cartList:[],
        totalMoney:0,//总金额
        totalNum:0//总数量
    },

    methods: {
        //获取用户名
        getName: function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.loginName = response.data;
            })
        },
        /**
         * 添加商品
         * @param tbItemId
         * @param num
         */
        addGoodsToCartList:function(itemId,num){
            axios.get('http://localhost:9107/cart/addGoodsToCartList.shtml',{
                params:{
                    itemId:itemId,
                    num:num
                },
                //允許跨域請求携帶cookie
                withCredentials:true
              }
            ).then(function(response){
                if(response.data.success){
                    window.location.href="success-cart.html"
                }else{
                    alert(response.data.message)
                }
            })
        }
    },
    created: function () {
        this.getName();
    }
})