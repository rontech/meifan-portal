$(function(){
                    $('.select_region').click(function(){
                        submitForm();
                    });
                    
                    $('.seatNums_MinNum').click(function(){
                        var minSeatObj=$('input:radio[name="seatNums.minNum"]:checked');
                        minSeatObj.next().attr("checked",'checked');
                     submitForm();
                    });

                    $('.priceRang_minPrice').click(function(){
                        var minPriceObj=$('input:radio[name="priceRange.minPrice"]:checked');
                        minPriceObj.next().attr("checked",'checked');
                        submitForm();
                    });

                    $('#surePrice').click(function(){
                        var minPrice = $('#lowPrice').val();
                        var maxPrice = $('#highPrice').val();
                        var re=/^[1-9]([0-9])*$/;
                        if(re.test(maxPrice) && minPrice == ''){
                        	$('.fill_priceRang_minPrice').val(0);
                            $('.fill_priceRang_minPrice').attr("checked",'checked');
                            $('.fill_priceRang_maxPrice').val(maxPrice);
                            $('.fill_priceRang_maxPrice').attr("checked",'checked');
                            submitForm();
                            return;
                        }
                        if(!re.test(maxPrice) || !re.test(minPrice)){
                        	return;
                        }
                        if(maxPrice < minPrice) {
                        	return;
                        }
                        
                        $('.fill_priceRang_minPrice').val(minPrice);
                        //$('input[name="priceRange.minPrice"]:eq(0)').attr("checked",'checked');
                        $('.fill_priceRang_minPrice').attr("checked",'checked');
                        $('.fill_priceRang_maxPrice').val(maxPrice);
                        $('.fill_priceRang_maxPrice').attr("checked",'checked');
                        submitForm();
                    });

                    $('#unlimitSeatNum').click(function(){
                        $('input[name="seatNums.minNum"]:eq(0)').attr("checked",'checked');
                        $('input[name="seatNums.maxNum"]:eq(0)').attr("checked",'checked');
                        submitForm();
                    });

                    $('#unlimitPrices').click(function(){
                        $('input[name="priceRange.minPrice"]:eq(0)').attr("checked",'checked');
                        $('input[name="priceRange.maxPrice"]:eq(0)').attr("checked",'checked');
                        submitForm();
                    });

                    $('.serviceType_checkbox').click(function(){
                        submitForm();
                    });

                    $('.brand_checkbox').click(function(){
                        submitForm();
                    });

                    $('.condtions_item').click(function(){
                        submitForm();
                    });
                    
                    
                }); 

                function unlimitedCondtions(){
                    var checkArray = document.getElementById("otherCondtion_group").getElementsByTagName("input");
                    for(var i=0; i<checkArray.length; i++){
                        if(checkArray[i].type=='checkbox'){
                            checkArray[i].checked=false;
                            }
                    }
                    submitForm();
                    
                }

                function unlimitSalons(){
                    var checkArray = document.getElementById("unlimitSalons_group").getElementsByTagName("input");
                    for(var i=0; i<checkArray.length; i++){
                        if(checkArray[i].type=='checkbox'){
                            checkArray[i].checked=false;
                            }
                    }
                    submitForm();
                }

                function unlimitServiceType(){
                    var checkArray = document.getElementById("unlimitServiceType_group").getElementsByTagName("input");
                    for(var i=0; i<checkArray.length; i++){
                        if(checkArray[i].type=='checkbox'){
                            checkArray[i].checked=false;
                            }
                    }
                    submitForm();
                }

                function unlimitedRegion(){
                    document.getElementById("unlimitedRegion").checked=true;
                    $('#areafield').empty();
                    submitForm();
                }

                function submitForm(){
                    document.getElementById("salonSearchForm").submit();
                }