var exec = require('cordova/exec');

exports.PrintImage = function (PrinterMac, ImageUrl, success, error) {
    exec(success, error, 'PrintImage', 'PrintAction', [PrinterMac, ImageUrl ]);
};

exports.SendCommandToPrinter = function (PrinterMac, CommandText, success, error) {
    exec(success, error, 'SendCommandToPrinter', 'SendCommandToPrinter', [PrinterMac, CommandText ]);
};

exports.GetPrinterLanguage = function (PrinterMac, success, error) {
    exec(success, error, 'GetPrinterLanguage', 'GetPrinterLanguage', [PrinterMac ]);
};
