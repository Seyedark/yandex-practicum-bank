{{/*
Expand the name of the chart.
*/}}
{{- define "account.name" -}}
{{- default .Chart.Name  }}
{{- end }}