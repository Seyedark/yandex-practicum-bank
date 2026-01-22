{{/*
Expand the name of the chart.
*/}}
{{- define "elastic.name" -}}
{{- default .Chart.Name  }}
{{- end }}