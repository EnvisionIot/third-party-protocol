/**
 * decode demo
 *
 * meta: {
 *      orgId: "{{orgId}}",
 *      netCompType: "http",
 *      http-uri: "/createDevice",
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
    const orgId = meta.orgId;

    const equips = jsonOriginalReq.equips;
    const points = jsonOriginalReq.points;
    const sites = jsonOriginalReq.sites;

    const timezoneMap = sites.map((site) => ({
        [site.id]: site.tz
    }))

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
        messageType: "CreateDevice",
        models: Object.keys(modelInfos).map((externalDeviceId) => ({
            externalDeviceId: externalDeviceId,
            measurepoints: modelInfos[externalDeviceId]
        })),
        devices: equips.map((equip) => ({
            externalDeviceId: getExternalDeviceId(equip.siteRef, equip.id),
            externalModelId: getExternalModelId(orgId, equip.modelType),
            timezone: getOrDefault(timezoneMap[equip.id], 'Asia/Shanghai')
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

function getExternalModelId(orgId, modelType) {
    return orgId + '-' + modelType;
}

function getOrDefault(value, defaultValue) {
    return value ? value : defaultValue;
}
