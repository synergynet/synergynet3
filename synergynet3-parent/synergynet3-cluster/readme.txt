Setting up OpenFire

--==[ users ]==--
Create a user for each possible device on the network, e.g:

red
blue
green
yellow
teacherconsole
projector1



--==[ groups ]==--

Groups identify what kind of device each user is. They are:



"multiplicitydevices"
---------------------

Should contain all users who are part of the SynergyNet network, e.g:

red
blue
green
yellow
teacherconsole
projector1

This group should also have 'Enable contact list group sharing' and
'share with additional users' ticked. Share with the groups
'tables', 'projectors' and 'teacherconsoles'




"tables"
--------

This refers to non-teacher, non-projector devices, e.g:

red
blue
green
yellow


This group should also have 'Enable contact list group sharing' and
'share with additional users' ticked. Share with the groups
'multiplicitydevices', 'projectors' and 'teacherconsoles'




"projectors"
------------

Refers to projectors, e.g:

projector1




"teacherconsoles"
-----------------

Despite its name, this should have at most one device that
acts as the controller (teacher console), e.g:

teacherconsole



================================================
================================================
---------====[ how to run ]====-----------------
================================================
================================================

Use VM parameters to run apps as a particular user, e.g:

-Dsynergynet3.xmpp.host=localhost 
-Dsynergynet3.xmpp.port=5222 
-Dsynergynet3.xmpp.username=red 
-Dsynergynet3.xmpp.password=password



