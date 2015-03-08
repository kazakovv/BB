package com.victor.sexytalk.sexytalk;

/**
 * Created by Victor on 09/12/2014.
 */
public class Statics {
    //Backendless KEYS

    public static final String KEY_USERNAME = "userName";
    public static final String KEY_RECEPIENT_IDS = "recepientIDs";
    public static final String KEY_RECEPIENT_EMAILS = "recepientsEmails";
    public static final String KEY_USERNAME_SENDER = "senderUsername";

    public static final String KEY_PARTNERS = "partners";

    public static final String KEY_MALE_OR_FEMALE = "maleOrFemale";


    public static final String TYPE_IMAGE_MESSAGE = "image";
    public static final String TYPE_TEXTMESSAGE = "textMessage";
    public static final String TYPE_KISS = "messageKiss";
    public static final String TYPE_CALENDAR_UPDATE = "calendarUpdate";
    public static final String TYPE_PARTNER_REQUEST ="partnerRequest";

    public static final String SEX_MALE = "Male";
    public static final String SEX_FEMALE = "Female";

    public static final String KEY_DATE_OF_BIRTH = "dateOfBirth";
    public static final String KEY_LOVE_MESSAGE = "loveMessage";
    public static final String KEY_NUMBER_OF_KISSES = "kissNumber";

    public static final String KEY_URL = "urlAddress";

    //keys za podavane na values m/u Private Days calendar i Love Days fragment
    public static final String CALENDAR_YEAR = "caldenarYear";
    public static final String CALENDAR_MONTH = "calendarMonth";
    public static final String CALENDAR_DAY = "calendarDay";
    public static final String AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE = "averageLengthOfCycle";
    public static final String TITLE_CYCLE = "titleCycle";
    public static final String FIRST_DAY_OF_CYCLE = "firstDayOfCycle";
    public static final String SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS = "sendSexyCalendarUpdate";


    //keys za tarsene v Backendless table s cycle statusi i titles
    public static final String KEY_MENSTRUATION = "Menstruation";
    public static final String KEY_OVULATION = "Ovulation";
    public static final String KEY_FOLLICULAR = "Follicular";
    public static final String KEY_LUTEAL = "Luteal";

    public static final String KEY_SEXY_STATUS = "sexyStatus";


    public static final String KEY_PARTNER_REQUEST = "partnerRequest";
    public static final String KEY_PARTNER_DELETE = "deletePartner";

    public static final String KEY_PARTNERS_SELECT_TAB = "selectPendingPartnerRequest";
    public static final String KEY_PARTNERS_SELECT_PENDING_REQUESTS = "selectPendingRequestsTab";

    public static final String GOOGLE_PROJECT_ID = "473995671207";

    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_MESSAGE_ID = "messageId";

    public static final String KEY_PROFILE_PIC_PATH = "profilePicPath";

    public static final int MESSAGE_TIME_TO_DISPLAY = 24; //24 chasa
    public static final int SHORT_SIDE_TARGET_THUMBNAIL = 100;
    public static final int SHORT_SIDE_TARGET_PIC = 500;

    public static final String KEY_SET_STATUS = "setStatus";

    public static final String BACKENDLESS_INVALID_LOGIN_OR_PASS_MESSAGE = "3003";
    public static final String BACKENDLESS_INVALID_EMAIL_PASSWORD_RECOVERY = "3020";
    public static final String BACKENDLESS_TABLE_NOT_FOUND_CODE = "1009";

    public static final String KEY_PARTNER_REQUEST_APPROVED = "partnerRequestApproved";

    public static final String KEY_USER_REQUESTING_TO_UPDATE_PARTNERS = "userRequestingToUpdatePartners";

    public static final int PICASSO_ROUNDED_CORNERS = 30;

    public static final String KEY_REFRESH_FRAGMENT_LOVE_BOX = "fragmentLoveBox";



    public static final String SHARED_PREFS="myPrefs";
    public static final String KEY_SAVED_EMAIL_FOR_LOGIN = "emailForLogin";
    //izpolzva se za reference dali ima chakashti zaiavki za partniori. Ako e true pokazvame buton na main t
    public static Boolean pendingPartnerRequest = false;

}

