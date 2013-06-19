<?php

define('BASE_PATH', base_url().'assets/'.TEMPLATES.'/');
define('JS_PATH', BASE_PATH.'js/');
define('CSS_PATH', BASE_PATH.'css/');
define('IMG_PATH', BASE_PATH.'img/');

$page = $this->uri->segment(1, null);

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <title><?=lang('main_title')?></title>
    
    <meta charset="utf-8"/>
    <!--<meta name="viewport" content="width=device-width, initial-scale=1.0">-->
    <meta name="description" content="An invitation-only social community where you can give and receive things you love for free." />
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    
    
    <!-- Loading Bootstrap -->
    <link rel='stylesheet' type='text/css' media='all' href="<?=CSS_PATH?>bootstrap.css" />
    <!--<link rel='stylesheet' type='text/css' media='all' href="<?=CSS_PATH?>bootstrap-responsive.css" />-->
    <!-- Loading Flat UI -->
    <link rel='stylesheet' type='text/css' media='all' href="<?=CSS_PATH?>flat-ui.css">
    <!-- Loading ge temp CSS -->
    <link rel='stylesheet' type='text/css' media='all' href="<?=BASE_PATH?>temp-gifteng.css" />
    <link rel='stylesheet' type='text/css' media='all' href="<?=BASE_PATH?>temp-pages.css" />
    <link rel='stylesheet' type='text/css' media='all' href="<?=BASE_PATH?>temp-gifteng-addon.css" />
    
    <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.css" />
    <!--[if lte IE 8]>
    <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.ie.css" />
    <![endif]-->
    
    
    <link rel="shortcut icon" href="<?=BASE_PATH?>images/favicon.ico">
    
    
    <!-- Load JS here for greater good =============================-->
    <script src="<?=JS_PATH?>jquery-1.8.3.min.js"></script>
    <script src="<?=JS_PATH?>jquery-ui-1.10.3.custom.min.js"></script>
    <script src="<?=JS_PATH?>jquery.ui.touch-punch.min.js"></script>
    <script src="<?=JS_PATH?>bootstrap.min.js"></script>
    <script src="<?=JS_PATH?>bootstrap-select.js"></script>
    <script src="<?=JS_PATH?>bootstrap-switch.js"></script>
    <script src="<?=JS_PATH?>flatui-checkbox.js"></script>
    <script src="<?=JS_PATH?>flatui-radio.js"></script>
    <script src="<?=JS_PATH?>jquery.tagsinput.js"></script>
    <script src="<?=JS_PATH?>jquery.placeholder.js"></script>
    <script src="<?=JS_PATH?>jquery.stacktable.js"></script>
    <script src="<?=JS_PATH?>application.js"></script>
    
    <script src="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.js"></script>
    <script src="<?=JS_PATH?>leaflet-providers.js"></script>
    
    
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements. All other JS at the end of file. -->
    <!--[if lt IE 9]>
      <script src="<?=JS_PATH?>html5shiv.js"></script>
    <![endif]-->
    
    
    <!--MASONRY -->
    <link rel='stylesheet' type='text/css' media='all' href="<?=CSS_PATH?>masonry.css" />
    <script type="text/javascript" src="<?=JS_PATH?>jquery.masonry.min.js"></script>
    <script type="text/javascript" src="<?=JS_PATH?>jquery.infinitescroll.min.js"></script>
    <script type="text/javascript" src="<?=JS_PATH?>modernizr-transitions.js"></script>
    
    
    
    
    <script type="text/javascript" src="<?=JS_PATH?>common.js"></script>
    
    <meta property="og:site_name" content="Gifteng"/>
    <meta property="og:url" content="http://gifteng.com/"/>
    <meta property="og:title" content="Gifteng ♥"/>
    <meta property="og:description" content="Give. Receive. Inspire."/>
    <meta property="og:image" content="<?=BASE_PATH?>images/logo.png"/>
    <meta property="og:type" content="website"/>
    
    <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
        
        ga('create', 'UA-40348949-1', 'gifteng.com');
        ga('send', 'pageview');
    </script>
    
</head>

<body>

<!-- ********** ********** ********** -->    
<!-- content starts here -->
<!-- ********** ********** ********** -->  

<!-- ge CONTENT -->
<div class="ge-container">


<div class="navbar navbar-fixed-top ge-navbar">
    <div class="navbar-inner">
    <div class="container">
        <div class="nav-collapse">
            <span class="nav">
                <a href="<?=base_url()?>index"><i class="gifteng"></i><sup>Beta</sup></a>
            </span>
            
            <div class="nav-collapse collapse">
                <ul class="nav">
                    <? if( isLogged() ): ?>
                    
                        <li<?=($page == "profile" ? ' class="active"' : '')?>><a href="<?=base_url()?>profile"><?=lang('main_submenu_profile')?></a></li>
                        <li<?=($page == "browse" ? ' class="active"' : '')?>><a href="<?=base_url()?>browse">BROWSE</a></li>
                        <li<?=($page == "post" ? ' class="active"' : '')?>><a href="<?=base_url()?>post">POST</a></li>
                    
                    <? else: ?>
                    
                        <li<?=($page == "invitation" ? ' class="active"' : '')?>><a href="<?=base_url()?>invitation/request">INVITATION REQUEST</a></li>
                        <li<?=($page == "invitation" ? ' class="active"' : '')?>><a href="<?=base_url()?>invitation/verify">INVITATION VERIFY</a></li>
                        <li<?=($page == "registration" ? ' class="active"' : '')?>><a href="<?=base_url()?>registration">REGISTRATION</a></li>
                    
                    <? endif; ?>
                        
                    <li<?=($page == "contact" ? ' class="active"' : '')?>><a href="<?=base_url()?>contact">CONTACT</a></li>
                </ul>
            </div>
            
            <ul class="nav pull-right">
                <li class="dropdown">
                    <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fui-user text-inverted"></i></a>
                    <div class="dropdown-menu" style="padding: 15px; padding-bottom: 10px;">
                        
                        <? if( isLogged() ): ?>
                            
                            <a href="<?=base_url()?>authentication/logout" class="btn btn-block btn-ge">SIGN OUT</a>
                        
                        <? else: ?>
                        
                            <form action="<?=base_url()?>authentication/login" method="post" accept-charset="UTF-8">
                                <input name="login_email" style="width: 142px; margin-bottom: 15px;" type="text" size="30" placeholder="Email address" />
                                <input name="login_password" style="width: 142px; margin-bottom: 15px;" type="password" size="30" placeholder="Password" />
                                <input name="login_remember_me" id="user_remember_me" style="float: left; margin-right: 10px;" type="checkbox" />
                                <label class="string optional" for="user_remember_me" style="color: #ffffff; text-shadow: none;">Remember me</label>
                                <input class="btn btn-ge" style="clear: left; width: 100%; height: 32px; font-size: 16px; font-weight: 400; padding-bottom: 30px;" type="submit" value="Sign In" />
                            </form>
                        
                        <? endif; ?>
                    </div>
                </li>
           </ul>
        </div>
    </div>
    </div>
</div><!--./navbar-->
