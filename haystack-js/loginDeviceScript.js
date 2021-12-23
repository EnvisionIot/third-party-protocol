/**
 * decode demo
 *
 * meta: {
 *      orgId: "{{orgId}}",
 *      netCompType: "http",
 *      http-uri: "/loginDevice",
 *      protocolGateway: "{{protocol-gateway-id}}"
 *  }
 *
 * originalReq: [TYPE] Json
 */
function getProtocol() {
    return 'haystack';
}

function encodeResponse(meta, originalReq, response) {
    return JSON.stringify(response);
}

function decode(meta, originalReq) {
    const jsonOriginalReq = JSON.parse(originalReq);
    const now = new Date().getTime();

    const keepOnline = jsonOriginalReq.keepOnline ? jsonOriginalReq.keepOnline : 600;
    const signMethod = jsonOriginalReq.signMethod ? jsonOriginalReq.signMethod : 'sha256';
    const timestamp = jsonOriginalReq.timestamp ? jsonOriginalReq.timestamp : now;
    const loginInfos = jsonOriginalReq.loginInfos;

    return {
        messageType: 'LoginDevice',
        keepOnline,
        signMethod,
        timestamp,
        devices: loginInfos.map((loginInfo) => ({
            externalDeviceId: getExternalDeviceId(loginInfo.siteRef, loginInfo.equipRef),
            sign: sign(loginInfo.deviceSecret,
                getExternalDeviceId(loginInfo.siteRef, loginInfo.equipRef),
                timestamp)
        }))
    };
}

function sign(secret, externalDeviceId, timestamp) {
    const params = {
        externalDeviceId,
        timestamp
    }

    let stringBuilder = '';
    Object.keys(params).sort().forEach((key) => {
        stringBuilder += key + params[key]
    });

    stringBuilder += secret;

    return sha256(stringBuilder);
}

function getExternalDeviceId(site, equipId) {
    return site ? site + '-' + equipId : equipId;
}
