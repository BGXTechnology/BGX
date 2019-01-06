<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>
<html>
<head>
<title>Reset password</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style type="text/css">
    *{ max-width: 100%; height: auto;}
		body{background: #474859; color: #fff;font-weight: 400; font-family: "Lucida Sans Unicode"; font-size:18px;}
		center{margin-top:50px;}
    .form-label{margin:0px 0px 10px;}
    .form-input{margin:0px 0px 20px;padding: 5px;}
    .form-input input{padding: 5px;}
    .form-button{}
    .form-button input{padding:10px 20px;background-color: #CF1B1B;color: #FFF;border: none;font-size:16px;-webkit-appearance: none;}
    .error-msg{ font-size: 16px; color:#ff4d4d;}
	</style>
</head>
<body>
  <center>
    <p><img src="/uploads/images/logo.png" alt="BGX" /></p>
    <div class="error-msg">
      <?php echo $msg; ?>
    </div>

    <?php echo form_open(current_url().'?locale='.$locale); ?>

    <p class="form-label"><?=$this->lang->line('new_password')?></p>
    <p class="form-input"><input type="password" name="password" value="" size="50" /></p>

    <p class="form-label"><?=$this->lang->line('confirm_new_password')?></p>
    <p class="form-input"><input type="password" name="passconf" value="" size="50" /></p>

    <div class="form-button"><input type="submit" value="<?=$this->lang->line('submit')?>" /></div>
    </form>
	</center>
  <script
			  src="https://code.jquery.com/jquery-2.2.4.min.js"
			  integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44="
			  crossorigin="anonymous"></script>
  <script type="text/javascript">
      $(document).ready(function () {
        var passwordRequired = $('.password-required');
        var passwordSpace = $('.password-space');
        var passwordMinSymbols = $('.password-min-symbols');
        var passwordNotMatch = $('.password-not-match');
        
        var errorStr = <?php echo json_encode($listMsg); ?>;
        if(passwordRequired.length == 0){
          $('.error-msg').append(errorStr[0]);
          passwordRequired = $('.password-required');
          passwordRequired.hide();
        }
        if(passwordSpace.length == 0){
          $('.error-msg').append(errorStr[1]);
          passwordSpace = $('.password-space');
          passwordSpace.hide();
        }
        if(passwordMinSymbols.length == 0){
          $('.error-msg').append(errorStr[2]);
          passwordMinSymbols = $('.password-min-symbols');
          passwordMinSymbols.hide();
        }
        if(passwordNotMatch.length == 0){
          $('.error-msg').append(errorStr[3]);
          passwordNotMatch = $('.password-not-match');
          passwordNotMatch.hide();
        }
        
        $('input').on('keyup', function () {
          var password = $('input[name="password"]').val();
          var confPassword = $('input[name="passconf"]').val();
          
          if(password.length > 0 ){
            passwordRequired.hide();
          }

          if(password.length == 0 || (password.length > 0 && password.trim().length > 0)){
            passwordSpace.hide();
          }

          if(password.length > 5 && password.length < 65){
            passwordMinSymbols.hide();
          }

          if(password.length > 5 && password == confPassword ){
            passwordNotMatch.hide();
          }
        });

        $('form').on('submit', function () {
          var password = $('input[name="password"]').val();
          var confPassword = $('input[name="passconf"]').val();
          var isFalse = false;

          if(password.length == 0){
            passwordRequired.show();
            
            isFalse = true;
          }

          if(password.length > 0 && password.trim().length == 0){
            passwordSpace.show();
            
            isFalse = true;
          }

          if(password.length < 6 || password.length > 64){
            passwordMinSymbols.show();

            isFalse = true;
          }

          if(password.length > 5 && password != confPassword ){
            passwordNotMatch.show();

            isFalse = true;
          }
          
          if(isFalse){
            return false;
          }
    
        })
      });
  </script>
</body>
</html>