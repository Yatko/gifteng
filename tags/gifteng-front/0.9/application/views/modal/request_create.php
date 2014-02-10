<script langauge="javascript">
    var requestCreateCallerElement;
    
    function startRequestModal(callerElement, type, adId) {
        requestCreateCallerElement = callerElement;
        
        var $requestAdId = $("#" + type + "_request_post_form input[name=requestAdId]");
        $requestAdId.val(adId);
        
        $("#member_request_post_form").removeClass('hide').addClass('hide');
        $("#business_request_post_form").removeClass('hide').addClass('hide');
        $("#" + type + "_request_post_form").removeClass('hide');
        
        if ( $('#requestContainer').length > 0 ) {
            showRequestModalStep2();
            $('#requestContainer').removeData("modal").modal('show');
        }
    }
    function startCannotRequestModal() {
        $('#cannotRequestContainer').removeData("modal").modal('show');
    }
    
    function request_modal(callerElement, type) {
        if ( $("#" + type + "_request_post_form").length === 0 ) {
            return;
        }
        if ( callerElement !== null ) {
            $(callerElement).attr("disabled", true);
        }

        var $requestAdId = $("#" + type + "_request_post_form input[name=requestAdId]");
        var $requestText = $("#" + type + "_request_post_form textarea[name=requestText]");

        $.ajax({
            type: 'POST',
            url: '<?=base_url()?>ajax/request',
            dataType: 'json',
            cache: false,
            data: {
                requestAdId: $requestAdId.val(),
                requestText: $requestText.val()
            }
        }).done(function(response) {
            if ( !response || response === '' ) {
                //TODO: empty result
            } else if ( response.hasOwnProperty('<?=AJAX_STATUS_ERROR?>') ) {
                //TODO
            } else if ( response.hasOwnProperty('<?=AJAX_STATUS_RESULT?>') ) {
                if ( callerElement !== null ) {
                    $(callerElement).removeAttr("disabled");
                }
                
                if ( $('.user_bookmark').length > 0 ) {
                    var num_bookmarks = response.<?=AJAX_STATUS_RESULT?>.<?=USER_BOOKMARKS_NUM?>;
                    $('.user_bookmark').text(num_bookmarks);
                }
                
                var adId = $requestAdId.val();
                $requestAdId.val('');
                $requestText.val('');

                if ( requestCreateCallerElement !== null ) {
                    var $element = $(requestCreateCallerElement);
                    $element.trigger('request_created', [adId]);
                }

                if ( $('#requestContainer').length > 0 ) {
                    $('#requestContainer').modal('hide');
                }
            } else {
                //TODO: unknown response received
            }
        }).fail(function(data) {
            //TODO
        });
    }
    
    function showRequestModalStep1() {
        $('#request_step_1').removeClass('hide');
        $('#request_step_2').addClass('hide');
    }
    function showRequestModalStep2() {
        $('#request_step_1').addClass('hide');
        $('#request_step_2').removeClass('hide');
    }
    
    $(function() {
        $('#requestContainer').on('hidden', function() {
            //showRequestModalStep1();
            showRequestModalStep2();
        });
    });
</script>



<div id="requestContainer" class="modal hide fade" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="row-fluid">
        <div class="span12">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <div class="modal-header-content">
                    <div class="ge-modal_header">
                        <label class="control-label" for="fieldset">
                            <h3>Type your message below</h3>
                        </label>
                    </div>
                </div>
            </div>


            <form id="business_request_post_form">
                <input type="hidden" name="requestAdId"/>
                <input type="hidden" name="requestText"/>
            
                <div class="modal-body">
                    <label class="control-label" for="fieldset">
                        <blockquote>
                            <p>Yippy! You are about to request a gift! Because its a "Company Gift" your request is instantly accepted.</p>
                        </blockquote>
                    </label>
                </div>

                <div class="modal-footer">
                    <div class="ge-modal_footer">
                        <fieldset>
                            <div class="row-fluid">
                                <div class="span12">
                                    <div class="control-group control-form">
                                        <div class="controls">
                                            <button type="button" class="span4 btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                                            <button onclick="request_modal(this, 'business');" type="button" class="span8 btn btn-ge">Send Request</button>
                                        </div>
                                    </div>
                                </div>
                            </div><!--./submit-->
                        </fieldset>
                    </div>
                </div>
            </form>
            

            <form id="member_request_post_form">
                <input type="hidden" name="requestAdId"/>

                <div id="request_step_1">

                    <div class="modal-body">
                        <label class="control-label" for="fieldset">
                            <blockquote>
                                <p>Yippy! You are about to request a gift! Because it’s a "Member’s Gift" your request will be pending until the giver accepts it.</p>
                            </blockquote>
                        </label>
                    </div>

                    <div class="modal-footer">
                        <div class="ge-modal_footer">
                            <fieldset>
                                <div class="row-fluid">
                                    <div class="span12">
                                        <div class="control-group control-form">
                                            <div class="controls">
                                                <button onclick="showRequestModalStep2();" type="button" class="span8 pull-right btn btn-ge" >Got it!</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </div>

                <div id="request_step_2" class="hide">
                    <div class="modal-body">
                        <fieldset>
                            <div class="row-fluid ge-input ge-message">
                                <div class="span12">
                                    <div class="row-fluid">
                                        <div class="span12 ge-text">
                                            <textarea name="requestText" id="request_message" placeholder="Your message ...">I would love to have your gift! Please accept my request!</textarea>
                                        </div>
                                    </div>
                                </div>
                            </div><!--./ge-input-->
                        </fieldset>
                    </div>
                    <div class="modal-footer">
                        <div class="ge-modal_footer">
                            <fieldset>
                                <div class="row-fluid">
                                    <div class="span12">
                                        <div class="control-group control-form">
                                            <div class="controls">
                                                <button type="button" class="span4 mobile-one btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                                                <button onclick="request_modal(this, 'member');" type="button" class="span8 mobile-three btn btn-ge">Send Request</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </div>
            </form>

        </div>
    </div>
</div>


<div id="cannotRequestContainer" class="modal hide fade" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="row-fluid">
        <div class="span12">
            
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <div class="modal-header-content">
                    <div class="ge-modal_header">
                        <label class="control-label" for="fieldset">
                            <h3>&nbsp;</h3>
                        </label>
                    </div>
                </div>
            </div>
            <div class="modal-body">
                <label class="control-label" for="fieldset">
                    <blockquote>
                        <p>Hi there! We appreciate your enthusiasm, but in order to request any more gifts you'll have to post one of your own. Click the button below to get started.</p>
                    </blockquote>
                </label>
            </div>
            <div class="modal-footer">
                <div class="ge-modal_footer">
                    <fieldset>
                        <div class="row-fluid">
                            <div class="span12">
                                <div class="control-group control-form">
                                    <div class="controls">
                                        <button type="button" class="span4 btn" data-dismiss="modal" aria-hidden="true">Later</button>
                                        <button onclick="startPostModal();" data-dismiss="modal" aria-hidden="true" type="button" class="span8 mobile-three btn btn-ge">Post a gift</button>
                                    </div>
                                </div>
                            </div>
                        </div><!--./submit-->
                    </fieldset>
                </div>
            </div>
            
        </div>
    </div>
</div>
