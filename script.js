
var ss = SpreadsheetApp.openByUrl("https://docs.google.com/spreadsheets/d/1wFnwGTyLh6yzvc5KnDzEzvwAV6oRAhNOX9d2gVil1Ik/edit#gid=0"); var sheet = ss.getSheetByName('PropertyData');
function doPost(e) {
var action = e.parameter.action;
if(action == 'addProperty') {
return addItem(e);
}
}
function addItem(e) {

//var id = sheet.getLastRow();
var image = "https://media.licdn.com/dms/image/C560BAQFCBcGN9S1x8w/company-logo_100_100/0/1677650636516?e=1689811200&v=beta&t=sy_vgt9xzGnzwpLiMBXkAU9skyiPjo1tok82y2AYFxo"


var who = e.parameter.vWho;
var owner_name = e.parameter.vOwnerName;

var phone = e.parameter.vPhone;
var email = e.parameter.vEmail;
var type_spin = e.parameter.vTypeSpin;
var subType_spin = e.parameter.vSubTypeSpin;
var name = e.parameter.vName;
var description = e.parameter.vDescription;
var state = e.parameter.vState;
var city_spin = e.parameter.vCitySpin;
var area = e.parameter.vArea;
var address = e.parameter.vAddress;
var rent_sale = e.parameter.vRent;
var status = e.parameter.vStatus;
var rat_s = e.parameter.vRated;
var feature_s = e.parameter.vFeature;
var min_price = e.parameter.vMinprice;
var max_price = e.parameter.vMaxprice;
var sq_feet = e.parameter.vSqFeet;
var year= e.parameter.vYear;
var bhk_spin = e.parameter.vBhkSpin;
var amenities = e.parameter.vAmenities;
var thumbnail = e.parameter.vThumbnail;
var prop_img = e.parameter.vPropImage;







sheet.appendRow( [who,owner_name, phone,email,type_spin,subType_spin,name,description,state,city_spin,area,address,rent_sale,status,rat_s,feature_s,min_price,max_price,sq_feet,year,bhk_spin,amenities,thumbnail,prop_img]);
return ContentService.createTextOutput("Success").setMimeType (ContentService.MimeType.TEXT);
}
