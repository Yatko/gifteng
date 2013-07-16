<?

$ad_id = $ad->id;
$request_id = $request->id;
$ad_creator = $ad->creator;
$ad_title = trim($ad->title);

?>

<div class="span6">
    <div class="well ge-well">

        <div class="row-fluid">
            <div class="ge-user">
                <? $this->load->view('element/user', array('user' => $ad_creator, 'canEdit' => false, 'small' => true)); ?>
            </div>
        </div>
        
        
        <? if( $ad->sold ): ?>
        
            
        
        <? elseif( $request->accepted ): ?>
        
            <div class="row-fluid">
                <div class="ge-action">
                    <div class="span6">
                        <button onclick="request_receive(<?=$request_id?>, <?=$ad_id?>);" type="button" class="btn btn-small btn-block btn-ge">Mark Received</button>
                    </div>
                </div><!--./ge-action-->
            </div>
        
        <? endif; ?>

        
        
        <div class="ge-messages">
            <div class="row-fluid">
                <div class="span12">

                    <div class="row-fluid">
                        <div class="ge-subject">
                            <a class="ge-title"><?=$ad_title?></a>
                        </div>
                    </div><!--./ge-subject-->
                    
                    <? $this->load->view('element/messages', array('messages' => $messages, 'ad' => $ad, 'to' => $ad_creator, 'canMessage' => true)); ?>
                    
                </div>
            </div>
        </div><!--./ge-messages-->

    </div><!--./ge-well-->
</div>
