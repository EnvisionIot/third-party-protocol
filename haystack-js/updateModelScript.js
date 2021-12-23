/**
 * decode demo
 *
 * meta: {
 *      orgId: "{{orgId}}",
 *      netCompType: "http",
 *      http-uri: "/updateModel",
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
    const partialUpdate = jsonOriginalReq.isPartialUpdate ? jsonOriginalReq.isPartialUpdate : true;
    const points = jsonOriginalReq.points;

    const modelInfos = {};
    points.forEach(point => {
        const externalDeviceId = getExternalDeviceId(point.siteRef, point.equipRef);
        const navName = point.navName;
        const mpType = translateHaystackType(point.kind);
        if (modelInfos[externalDeviceId]) {
            modelInfos[externalDeviceId][navName] = mpType;
        } else {
            modelInfos[externalDeviceId] = {
                [navName]: mpType
            }
        }
    });

    return {
        messageType: "UpdateModel",
        partialUpdate,
        models: Object.keys(modelInfos).map((externalDeviceId) => ({
            externalDeviceId: externalDeviceId,
            measurepoints: modelInfos[externalDeviceId]
        }))
    };
}

function translateHaystackType(type) {
    if ('Str' === type) {
        return 'TEXT';
    } else if ('Number' === type) {
        return 'FLOAT';
    } else if ('Bool' === type) {
        return 'BOOL';
    }
}

function getExternalDeviceId(site, equipId) {
    return site ? site + '-' + equipId : equipId;
}
